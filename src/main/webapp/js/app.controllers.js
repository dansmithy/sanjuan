
function MainController($route, $xhr, userManager, version) {

	this.version = version;
	this.userManager = userManager;
	this.$xhr = $xhr;
	$xhr.defaults.headers.put["Content-Type"] = "application/json";
	$xhr.defaults.headers.post["Content-Type"] = "application/json";
	$xhr.defaults.headers.common["Content-Type"] = "application/json";

	$route.when("/games", { template : "partials/games.html", controller : GamesController });
	$route.when("/games/:gameId", { template : "partials/game.html", controller : GameController });
	$route.when("/admin", { template : "partials/admin.html", controller : AdminController });
	$route.otherwise( { "redirectTo" : "/games" });
	$route.onChange(function() {
        $route.current.scope.params = $route.current.params;
    });
	
	this.checkLoggedIn();	
}

MainController.$inject = ["$route", "$xhr", "userManager", "version" ];

MainController.prototype = {

		checkLoggedIn : function() {
			this.$xhr("GET", "loginDetails", this.authenticationCallback);
		},
		
		login : function() {
			this.userManager.authenticating();
			this.$root.$eval(); // required to update view when pressing return, so that if still an error it is displayed
			if (this.userManager.credentials.username === "") {
				this.userManager.error = "You must supply a valid username.";
				this.userManager.unauthenticated();
				return;
			}
			this.$xhr("POST", "j_spring_security_check", this.userManager.credentials, this.authenticationCallback);
		},
		
		authenticationCallback : function(code, response) {
			if (response && response.username) {
				this.userManager.authenticated(response);
			} else if (code === 403) {
				this.userManager.unauthenticated("Unknown username and/or password");
			} else {
				this.userManager.unauthenticated();
			}
		},
		
		logout : function() {
			this.userManager.unauthenticated();
			this.$xhr("GET", "j_spring_security_logout", angular.noop);
		}
		
};

function GameController($xhr, $defer, userManager, gameService, cardService) {
	
	
	this.userManager = userManager;
	this.$defer = $defer;
	this.gameService = gameService;
	this.cardService = cardService;
	
	this.responder = { "mode" : "none", "template" : "partials/none.html" };
	this.roleStatusMap = { "builder" : "to build", "councillor" : "cards to keep", "producer" : "to produce", "trader" : "to trade", "prospector" : "to do." };
	
	this.role = "undecided";
	
	this.cardTypes = {};
	this.cardMap = {};
	
	this.allRoles = [ "governor", "builder", "producer", "trader", "councillor", "prospector" ];
	
	this.tariff = [ 1, 1, 2, 2, 3 ];
	
	this.$xhr = $xhr;
	
	this.isActivePlayer = true;
	
	this.statusText = {};
	
//	this.$defer(this.autoRefresh, 5000);
//	this.updateGame();

	var self = this;
	
	if (this.userManager.isAuthenticated()) {
		this.gameService.updateGame(this.params.gameId, 0, this.gameCallback);
	}
	this.$watch("userManager.user", function(username) {
		if (username) {
			self.gameService.updateGame(self.params.gameId, 0, self.gameCallback);
		}
	});

};
GameController.$inject = [ "$xhr", "$defer", "userManager", "gameService", "cardService" ];
GameController.prototype = {
		
		autoRefresh : function() {
			if (!this.isActivePlayer) {
				this.updateGame();
				this.$defer(this.autoRefresh, 5000);
			}
		},
		
		updateGame : function() {
			this.$xhr("GET", "ws/games/" + this.params.gameId, this.gameCallback);
		},
		
		gameCallback : function(code, response) {
			if (code === 200) {
				this.processGame(response);
			} else if (code === 404) {
				
			}
		},
		
		getResponder : function(playerName) {
			return this.isSelf(playerName) ? this.responder : angular.noop;
		},
		
		roleImageUrl : function(role) {
			return angular.isDefined(role) ? "images/" + role + ".gif" : "";
		},
		
		clickRoleCard : function(role) {
			if (this.responder.mode == "role_choice") {
				this.responder.response.role = role;
			}
		},
		
		cardDescription : function(card) {
			console.debug(this.cardMap);
			return "nothing";
//			return this.cardTypes[this.cardMap[card]].description;
		},
		
		showSelected : function(role) {
			return this.responder.mode == "role_choice" && role == this.responder.response.role;
		},
		
		processGame : function(game) {
			game.$round = game.rounds[game.roundNumber-1];
			game.$round.$phase = game.$round.phases[game.$round.phaseNumber-1];
			
			//reorder players
			while (!this.isNameActivePlayer(game.players[0].name)) {
				game.players.push(game.players.shift());
			}
			
			var usedRoles = [ "governor" ];
			var unusedRoles = [];
			angular.forEach(game.$round.phases, function(phase) {
				if (angular.isDefined(phase.role)) {
					usedRoles.push(phase.role.toLowerCase());
				}
				
			});
			angular.forEach(this.allRoles, function(role) {
				if (angular.Array.indexOf(usedRoles, role) == -1) {
					unusedRoles.push(role);
				}
			});
				
			game.$usedRoles = usedRoles;
			game.$unusedRoles = unusedRoles;			
			if (game.$round.$phase.state === "PLAYING") {
				game.$currentRole = game.$usedRoles.pop();
				game.$round.$phase.$play = game.$round.$phase.plays[game.$round.$phase.playNumber-1];
				game.currentPlayerName = game.$round.$phase.$play.player;
				this.isActivePlayer = this.isNameActivePlayer(game.currentPlayerName);
				if (this.isActivePlayer) {
					this.responder = this.determineActivePlayerResponder(game);
				} else {
					this.statusText = { "waiting" : true, "message" : "Waiting for <strong>" + game.currentPlayerName + "</strong> to choose what " + this.roleStatusMap[game.$currentRole] };
				}
			} else if (game.$round.$phase.state === "AWAITING_ROLE_CHOICE") {
				game.currentPlayerName = game.$round.$phase.leadPlayer;
				this.isActivePlayer = this.isNameActivePlayer(game.currentPlayerName);
				if (this.isActivePlayer) {
					this.responder = new GovernorChoiceResponse(this.$xhr, game, this.gameCallback);
				} else {
					this.statusText = { "waiting" : true, "message" : "Waiting for <strong>" + game.currentPlayerName + "</strong> to choose role." };
				}
			} else {
				// completed
				// not sure
			}
			
			if (!this.isActivePlayer) {
				this.responder = new InactivePlayerResponder(this.$xhr, game, this.gameCallback);
				this.gameService.startGameUpdates(game.gameId, game.version, this.gameCallback);
			} else {
				this.statusText = { "waiting" : false, "message" : "It's your turn!" };
				this.gameService.stopGameUpdates();
			}
		},
		
		determineActivePlayerResponder : function(game) {
			if (game.$round.$phase.role === "BUILDER") {
				return new BuilderResponder(this.$xhr, this.cardService, game, this.gameCallback);
			} else if (game.$round.$phase.role === "PROSPECTOR") {
				return new ProspectorResponder(this.$xhr, game, this.gameCallback);
			} else if (game.$round.$phase.role === "COUNCILLOR") {
				return new CouncillorResponder(this.$xhr, game, this.gameCallback);
			} else if (game.$round.$phase.role === "PRODUCER") {
				return new ProducerResponder(this.$xhr, this.cardService, game, this.gameCallback);
			} else if (game.$round.$phase.role === "TRADER") {
				return new TraderResponder(this.$xhr, this.cardService, this.userManager, game, this.gameCallback);
			} else {
				return new DoSomethingResponder(this.$xhr, game, this.gameCallback);
			}
		},
		
		isNameActivePlayer : function(playerName) {
			return this.userManager.user.username === playerName;
		},
		
		isSelf : function(playerName) {
			return playerName === this.userManager.user.username;
		}
		
		
//		computeIsActivePlayer : function(game, isRoleChosen) {
//			return this.userManager.username !== this.computeActivePlayer(game, isRoleChosen);
//		},
//
//		computeActivePlayer : function(game, isRoleChosen) {
//			if (!isRoleChosen) {
//				return this.$round.$phase.leadPlayer;
//			}
//		},

		
};

function GovernorChoiceResponse($xhr, game, gameCallback) {
	this.$xhr = $xhr;
	this.response = { "role" : game.$unusedRoles[0] };
	this.mode = "role_choice";
	this.template = "partials/chooseRole.html";
	this.game = game;
	this.gameCallback = gameCallback;
};

GovernorChoiceResponse.prototype = {
	
	sendResponse : function() {
		var modifiedResponse = angular.Object.copy(this.response);
		modifiedResponse.role = modifiedResponse.role.toUpperCase();
		this.$xhr("PUT", "ws/games/" + this.game.gameId + "/rounds/" + this.game.roundNumber + "/phases/" + this.game.$round.phaseNumber + "/role", modifiedResponse, this.gameCallback);
	}
};


function DoSomethingResponder($xhr, game, gameCallback) {
	this.$xhr = $xhr;
	this.response = "OK";
	this.emptyResponse = { "skip" : true };
	this.template = "partials/otherRole.html";
	this.mode = "do_something";
	this.game = game;
	this.gameCallback = gameCallback;
};

DoSomethingResponder.prototype = {
	
	commitSkipResponse : function() {
		this.$xhr("PUT", "ws/games/" + this.game.gameId + "/rounds/" + this.game.roundNumber + "/phases/" + this.game.$round.phaseNumber + "/plays/" + this.game.$round.$phase.playNumber + "/decision", this.emptyResponse, this.gameCallback);
	}
};

function ProspectorResponder($xhr, game, gameCallback) {
	this.$xhr = $xhr;
	this.response = { };
	this.template = "partials/prospector.html";
	this.mode = "do_something";
	this.game = game;
	this.gameCallback = gameCallback;
};

ProspectorResponder.prototype = {
	
	commitResponse : function() {
		this.$xhr("PUT", "ws/games/" + this.game.gameId + "/rounds/" + this.game.roundNumber + "/phases/" + this.game.$round.phaseNumber + "/plays/" + this.game.$round.$phase.playNumber + "/decision", this.response, this.gameCallback);
	}
};

function CouncillorResponder($xhr, game, gameCallback) {
	this.$xhr = $xhr;
	this.offered = game.$round.$phase.$play.offered;
	this.response = { "councilDiscarded" : [] };
	this.emptyResponse = { "skip" : true };
	this.cardsToDiscard = this.offered.councilOffered.length - this.offered.councilRetainCount;
	this.template = "partials/councillor.html";
	this.mode = "do_something";
	this.game = game;
	this.gameCallback = gameCallback;
};

CouncillorResponder.prototype = {
	
	commitResponse : function() {
		this.$xhr("PUT", "ws/games/" + this.game.gameId + "/rounds/" + this.game.roundNumber + "/phases/" + this.game.$round.phaseNumber + "/plays/" + this.game.$round.$phase.playNumber + "/decision", this.response, this.gameCallback);
	},
	
	choicesMade : function() {
		return this.response.councilDiscarded.length === this.cardsToDiscard;
	},
	
	handCardSelectedType : function(handCard) {
		if (this.isDiscardCard(handCard)) {
			return "hand-to-build";
		}
	},	
	
	councilCardSelectedType : function(councilCard) {
		if (this.isDiscardCard(councilCard)) {
			return "hand-to-build";
		}
	},
	
	clickCouncilCard : function(councilCard) {
		if (this.isDiscardCard(councilCard)) {
			this.keepCard(councilCard);
		} else {
			this.addCardToDiscarded(councilCard);
		}
	},
	
	clickHandCard : function(handCard) {
		if (!this.offered.councilCanDiscardHandCards) {
			return;
		}
		if (this.isDiscardCard(handCard)) {
			this.keepCard(handCard);
		} else {
			this.addCardToDiscarded(handCard);
		}
	},	
	
	isDiscardCard : function(card) {
		return angular.Array.indexOf(this.response.councilDiscarded, card) !== -1;
	},
	
	addCardToDiscarded : function(card) {
		var cardsAlreadyAdded = this.response.councilDiscarded.length;
		var indexToAdd = cardsAlreadyAdded === this.cardsToDiscard ? this.cardsToDiscard - 1 : cardsAlreadyAdded;
		this.response.councilDiscarded[indexToAdd] = card;
	},
	
	keepCard : function(card) {
		angular.Array.remove(this.response.councilDiscarded, card);
	},
	
	commitSkipResponse : function() {
		this.$xhr("PUT", "ws/games/" + this.game.gameId + "/rounds/" + this.game.roundNumber + "/phases/" + this.game.$round.phaseNumber + "/plays/" + this.game.$round.$phase.playNumber + "/decision", this.emptyResponse, this.gameCallback);
	}
	
};


function BuilderResponder($xhr, cardService, game, gameCallback) {
	this.$xhr = $xhr;
	this.cardService = cardService;
	this.offered = game.$round.$phase.$play.offered;
	this.response = { "build" : -1, "payment" : [] };
	this.currentBuildCost = -1;
	this.emptyResponse = { "skip" : true };
	this.template = "partials/builder.html";
	this.mode = "do_something";
	this.game = game;
	this.gameCallback = gameCallback;
};

BuilderResponder.prototype = {
	
	sendResponse : function() {
		this.$xhr("PUT", "ws/games/" + this.game.gameId + "/rounds/" + this.game.roundNumber + "/phases/" + this.game.$round.phaseNumber + "/plays/" + this.game.$round.$phase.playNumber + "/decision", this.response, this.gameCallback);
	},
	
	commitSkipResponse : function() {
		this.$xhr("PUT", "ws/games/" + this.game.gameId + "/rounds/" + this.game.roundNumber + "/phases/" + this.game.$round.phaseNumber + "/plays/" + this.game.$round.$phase.playNumber + "/decision", this.emptyResponse, this.gameCallback);
	},
	
	choicesMade : function() {
		return this.response.build != -1 && this.response.payment.length === this.currentBuildCost;
	},
	
	handCardSelectedType : function(handCard) {
		if (handCard === this.response.build) {
			return "hand-to-build";
		} else if (this.isPaymentCard(handCard)) {
			return "hand-payment";
		}
	},
	
	isPaymentCard : function(card) {
		return angular.Array.indexOf(this.response.payment, card) !== -1;
	},
	
	removePayment : function(card) {
		angular.Array.remove(this.response.payment, card);
	},
	
	clearToBuild : function() {
		this.response.build = -1;
		this.currentBuildCost = -1;
	},
	
	selectToBuild : function(card) {
		this.response.build = card;
		var cardType = this.cardService.cardType(card);
		this.currentBuildCost = cardType.buildingCost - (cardType.category === "VIOLET" ? this.offered.builderDiscountOnViolet : this.offered.builderDiscountOnProduction);
		this.currentBuildCost = this.currentBuildCost < 0 ? 0 : this.currentBuildCost;
		if (this.response.payment.length > this.currentBuildCost) {
			this.response.payment = angular.Array.limitTo(this.response.payment, this.currentBuildCost);
		}
	},
	
	clickHandCard : function(handCard) {
		if (handCard === this.response.build) {
			this.clearToBuild();
		} else {
			if (this.response.build === -1) {
				if (this.isPaymentCard(handCard)) {
					this.removePayment(handCard);
				}
				this.selectToBuild(handCard);
				
			} else { // have something to build already
				if (this.isPaymentCard(handCard)) {
					this.removePayment(handCard);
				} else {
					var cardsAlreadyAdded = this.response.payment.length;
					var indexToAdd = cardsAlreadyAdded === this.currentBuildCost ? this.currentBuildCost - 1 : cardsAlreadyAdded;
					this.response.payment[indexToAdd] = handCard;
				}
				
			}
			
		}
	}
};

function ProducerResponder($xhr, cardService, game, gameCallback) {
	this.$xhr = $xhr;
	this.cardService = cardService;
	this.offered = game.$round.$phase.$play.offered;
	this.response = { "productionFactories" : [] };
	this.emptyResponse = { "skip" : true };
	this.template = "partials/producer.html";
	this.mode = "do_something";
	this.game = game;
	this.gameCallback = gameCallback;
};

ProducerResponder.prototype = {
	
	commitSkipResponse : function() {
		this.$xhr("PUT", "ws/games/" + this.game.gameId + "/rounds/" + this.game.roundNumber + "/phases/" + this.game.$round.phaseNumber + "/plays/" + this.game.$round.$phase.playNumber + "/decision", this.emptyResponse, this.gameCallback);
	},
	
	sendResponse : function() {
		this.$xhr("PUT", "ws/games/" + this.game.gameId + "/rounds/" + this.game.roundNumber + "/phases/" + this.game.$round.phaseNumber + "/plays/" + this.game.$round.$phase.playNumber + "/decision", this.response, this.gameCallback);
	},
	
	choicesMade : function() {
		return this.response.productionFactories.length >= 1 && this.response.productionFactories.length <= this.offered.goodsCanProduce;
	},
	
	clickBuildingCard : function(buildingCard) {
		// check is production building- or is in list of possibles!
		if (angular.Array.indexOf(this.offered.factoriesCanProduce, buildingCard) == -1) {
			return;
		} 
		if (this.isChosenFactory(buildingCard)) {
			this.deselectFactory(buildingCard);
		} else {
			this.addFactory(buildingCard);
		}
	},	
	
	isChosenFactory : function(card) {
		return angular.Array.indexOf(this.response.productionFactories, card) != -1;
	},
	
	deselectFactory : function(card) {
		angular.Array.remove(this.response.productionFactories, card);
	},
	
	addFactory : function(card) {
		var factoriesAlreadyChosen = this.response.productionFactories.length;
		var indexToAdd = factoriesAlreadyChosen === this.offered.goodsCanProduce ? this.offered.goodsCanProduce - 1 : factoriesAlreadyChosen;
		this.response.productionFactories[indexToAdd] = card;
	},
	
	buildingCardSelectedType : function(buildingCard) {
		if (this.isChosenFactory(buildingCard)) {
			return "hand-to-build";
		}
	}
	
};

function TraderResponder($xhr, cardService, userManager, game, gameCallback) {
	this.$xhr = $xhr;
	this.cardService = cardService;
	this.userManager = userManager;
	this.goods = this.getPlayer(game, userManager.user.username).goods;
	this.offered = game.$round.$phase.$play.offered;
	this.prices = game.$round.$phase.tariff.prices;
	this.response = { "productionFactories" : [] };
	this.emptyResponse = { "skip" : true };
	this.template = "partials/trader.html";
	this.mode = "do_something";
	this.game = game;
	this.gameCallback = gameCallback;
};

TraderResponder.prototype = {
	
	getPlayer : function(game, playerName) {
		for (var i=0; i<game.players.length; i++) {
			if (game.players[i].name === playerName) {
				return game.players[i];
			}
		}
	},
	
	commitSkipResponse : function() {
		this.$xhr("PUT", "ws/games/" + this.game.gameId + "/rounds/" + this.game.roundNumber + "/phases/" + this.game.$round.phaseNumber + "/plays/" + this.game.$round.$phase.playNumber + "/decision", this.emptyResponse, this.gameCallback);
	},
	
	sendResponse : function() {
		this.$xhr("PUT", "ws/games/" + this.game.gameId + "/rounds/" + this.game.roundNumber + "/phases/" + this.game.$round.phaseNumber + "/plays/" + this.game.$round.$phase.playNumber + "/decision", this.response, this.gameCallback);
	},
	
	choicesMade : function() {
		return this.response.productionFactories.length >= 1 && this.response.productionFactories.length <= this.offered.goodsCanTrade;
	},
	
	clickBuildingCard : function(buildingCard) {
		// check is production building- or is in list of possibles!
		if (!this.hasGoodToSell(buildingCard)) {
			return;
		}
		if (this.isChosenFactory(buildingCard)) {
			this.deselectFactory(buildingCard);
		} else {
			this.addFactory(buildingCard);
		}
	},	
	
	hasGoodToSell : function(card) {
		return angular.isDefined(this.goods[card]);
	},
	
	isChosenFactory : function(card) {
		return angular.Array.indexOf(this.response.productionFactories, card) != -1;
	},
	
	deselectFactory : function(card) {
		angular.Array.remove(this.response.productionFactories, card);
	},
	
	addFactory : function(card) {
		var factoriesAlreadyChosen = this.response.productionFactories.length;
		var indexToAdd = factoriesAlreadyChosen === this.offered.goodsCanTrade ? this.offered.goodsCanTrade - 1 : factoriesAlreadyChosen;
		this.response.productionFactories[indexToAdd] = card;
	},
	
	buildingCardSelectedType : function(buildingCard) {
		if (this.isChosenFactory(buildingCard)) {
			return "hand-to-build";
		}
	},
	
	calculatePayment : function() {
		var payment = 0;
		angular.forEach(this.response.productionFactories, function(factory) {
			var cardType = this.cardService.cardType(factory);
			var price = this.prices[cardType.buildingCost - 1];
			payment += price;
		}, this);
		return payment;
	}
	
};

function InactivePlayerResponder($xhr, game, gameCallback) {
	this.$xhr = $xhr;
	this.response = "OK";
	this.template = "partials/none.html";
	this.mode = "do_something";
	this.game = game;
	this.gameCallback = gameCallback;
};

InactivePlayerResponder.prototype = {
	
};


function GamesController($xhr, $location, userManager) {
	
	var self = this;
	
	this.$xhr = $xhr;
	this.$location = $location;
	this.userManager = userManager;
	this.games = [];
	this.openGames = [];
	
	this.$watch("userManager.user", function(username) {
		if (username) {
			self.refresh();
		}
	});
}

GamesController.$inject = [ "$xhr", "$location", "userManager" ];

GamesController.prototype = {
	
	refresh : function() {
		this.$xhr("GET", "ws/games?player=" + this.userManager.user.username, this.gamesCallback);
		this.$xhr("GET", "ws/games?state=RECRUITING", this.openGamesCallback);
	},
	
	gamesCallback : function(code, response) {
		this.games = response;
	},
	
	openGamesCallback : function(code, response) {
		var username = this.userManager.user.username;
		this.openGames = angular.Array.filter(response, function(game) {
			var include = true;
			angular.forEach(game.players, function(player) {
				if (username === player.name) {
					include = false;
				}
			});
			return include;
		});
	},
	
	createGame : function() {
		var self = this;
		this.$xhr("POST", "ws/games", this.userManager.user.username, function(code, response) {
			self.refresh();
		});
	},
	
	joinGame : function(game) {
		var self = this;
		this.$xhr("POST", "ws/games/" + game.gameId + "/players", this.userManager.user.username, function(code, response) {
			self.refresh();
		});
	},
	
	startGame : function(game) {
		var self = this;
		this.$xhr("PUT", "ws/games/" + game.gameId + "/state", "PLAYING", function(code, response) {
			self.$location.updateHash("/games/" + response.gameId);
		});
	},
	
	deleteGame : function(game) {
		var self = this;
		this.$xhr("DELETE", "ws/games/" + game.gameId, function(code, response) {
			self.refresh();
		});
		
	}
		
};



function AdminController($xhr, userManager) {
	
	this.userManager = userManager;
	this.$xhr = $xhr;
	this.adding = false;
	
	this.users = [];
	this.newUser = {};
	this.getUsers();
	

};
AdminController.$inject = [ "$xhr", "userManager" ];
AdminController.prototype = {
		
		getUsers : function() {
			this.$xhr("GET", "ws/users", this.userCallback);			
		},
		
		userCallback : function(code, response) {
			this.users = response;
		},
		
		update : function(user) {
			this.$xhr("PUT", "ws/users/" + user.username, user, this.getUsers);
		},
		
		create : function(user) {
			this.$xhr("POST", "ws/users", user, this.getUsers);
		},
		
		createNew : function() {
			this.adding = true;
		},
		
		cancelAdd : function() {
			this.adding = false;
		}
		
};
