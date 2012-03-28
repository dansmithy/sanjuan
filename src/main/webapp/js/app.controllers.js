
function MainController($route, $xhr, userManager, version) {

	this.version = version;
	this.userManager = userManager;
	this.$xhr = $xhr;
	$xhr.defaults.headers.put["Content-Type"] = "application/json";
	$xhr.defaults.headers.post["Content-Type"] = "application/json";
	$xhr.defaults.headers.common["Content-Type"] = "application/json";

	$route.when("/games", { template : "partials/games.html?v=" + version, controller : GamesController });
	$route.when("/games/:gameId", { template : "partials/game.html?v=" + version, controller : GameController });
	$route.when("/admin", { template : "partials/admin.html?v=" + version, controller : AdminController });
	$route.when("/cards", { template : "partials/cards.html?v=" + version, controller : CardsController });
	$route.otherwise( { "redirectTo" : "/games" });
	$route.onChange(function() {
        $route.current.scope.params = $route.current.params;
    });
	
	this.checkLoggedIn();	
}

MainController.$inject = ["$route", "$xhr", "userManager", "version" ];

MainController.prototype = {

		checkLoggedIn : function() {
			this.$xhr("GET", "/ws/auth/user?" + new Date().getTime(), this.authenticationCallback);
		},
		
		authenticationCallback : function(code, response) {
			if (response && response.username) {
				this.userManager.authenticated(response);
			} else {
				this.userManager.unauthenticated();
			}
		},
		
		logout : function() {
			this.userManager.unauthenticated();
			this.$xhr("GET", "/logout", angular.noop);
		}
};

function GameController($xhr, $defer, userManager, gameService, cardService, version) {
	
	
	this.userManager = userManager;
	this.$defer = $defer;
	this.gameService = gameService;
	this.cardService = cardService;
	this.version = version;
	
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
	this.$watch("userManager.state", function(state) {
		if (state === "authenticated") {
			self.gameService.updateGame(self.params.gameId, 0, self.gameCallback);
		}
	});

};
GameController.$inject = [ "$xhr", "$defer", "userManager", "gameService", "cardService", "version" ];
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
			return "nothing";
//			return this.cardTypes[this.cardMap[card]].description;
		},
		
		showSelected : function(role) {
			return this.responder.mode == "role_choice" && role == this.responder.response.role;
		},
		
		switchToLowercase : function(array) {
			for (var i=0; i<array.length; i++) {
				array[i] = array[i].toLowerCase();
			}
			return array;
		},
		
		processGame : function(game) {
			game.$round = game.currentRound;
			game.$round.$phase = game.$round.currentPhase;
			
			//reorder players
			while (!this.isNameActivePlayer(game.players[0].name)) {
				game.players.push(game.players.shift());
			}
			
			var usedRoles = this.switchToLowercase(game.$round.playedRoles);
			var unusedRoles = this.switchToLowercase(game.$round.remainingRoles);
			
			game.$usedRoles = usedRoles;
			game.$unusedRoles = unusedRoles;
			
			if (game.complete) {
				this.responder = new CompletedGameResponder(game);
			} else if (game.$round.state === "GOVERNOR") {
				game.$currentRole = game.$round.currentRole.toLowerCase();
				game.currentPlayerName = game.$round.governorPhase.currentPlayer;
				this.isActivePlayer = this.isNameActivePlayer(game.currentPlayerName);
				if (this.isActivePlayer) {
					game.$round.$governorStep = game.$round.governorPhase.currentStep;
					this.responder = new GovernorPhaseResponse(this.$xhr, game, this.gameCallback, this.cardService);
				} else {
					this.statusText = { "waiting" : true, "message" : "Waiting for <strong>" + game.currentPlayerName + "</strong> to make their choice for the Governor phase" };
				}
			} else if (game.$round.$phase.state === "PLAYING") {
				game.$currentRole = game.$round.currentRole.toLowerCase();
				game.$round.$phase.$play = game.$round.$phase.currentPlay;
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
					this.responder = new GovernorChoiceResponse(this.$xhr, game, this.gameCallback, this.cardService);
				} else {
					this.statusText = { "waiting" : true, "message" : "Waiting for <strong>" + game.currentPlayerName + "</strong> to choose role." };
				}
			} else {
				// completed
				// not sure
			}
			
			if (!game.complete) {
				if (!this.isActivePlayer) {
					this.responder = new InactivePlayerResponder(this.$xhr, game, this.gameCallback);
					this.gameService.startGameUpdates(game.gameId, game.version, this.gameCallback);
				} else {
					this.statusText = { "waiting" : false, "message" : "It's your turn!" };
					this.gameService.stopGameUpdates();
				}
			} else {
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
			return this.userManager.user && this.userManager.user.username === playerName;
		},
		
		isSelf : function(playerName) {
			return this.userManager.user && this.userManager.user.username === playerName;
		},
		
		arrayContains : function(array, item) {
			return angular.Array.contains(array, item);
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


function CompletedGameResponder(game) {
	this.game = game;
	this.mode = "do_something";
	this.template = "partials/completed.html";
	this.isWinner = (game.players[0].name === game.winner);
};

function GovernorChoiceResponse($xhr, game, gameCallback, cardService) {
	this.$xhr = $xhr;
	this.response = { "role" : game.$unusedRoles[0], "useLibrary" : true };
	this.mode = "role_choice";
	this.template = "partials/chooseRole.html";
	this.game = game;
	this.gameCallback = gameCallback;
	this.needToChooseLibrary = this.checkIfLibraryNeedsToBeChosen(cardService);
};

GovernorChoiceResponse.prototype = {
	
	sendResponse : function() {
		var modifiedResponse = angular.Object.copy(this.response);
		modifiedResponse.role = modifiedResponse.role.toUpperCase();
		this.$xhr("PUT", "ws/games/" + this.game.gameId + "/rounds/" + this.game.roundNumber + "/phases/" + this.game.$round.phaseNumber + "/role", modifiedResponse, this.gameCallback);
	},

	checkIfLibraryNeedsToBeChosen : function(cardService) {
	    if (this.game.$round.phaseNumber !== 1) {
	        return false;
	    }
	    var buildings = this.game.players[0].buildings;
        for (var cardIndex = 0; cardIndex < buildings.length; cardIndex++) {
            if (cardService.isLibrary(buildings[cardIndex])) {
                return true;
            }
        }
        return false;
	}


};

function GovernorPhaseResponse($xhr, game, gameCallback, cardService) {
	this.$xhr = $xhr;
	this.options = game.$round.$governorStep;
	this.response = { "cardsToDiscard" : [ ] };
	this.mode = "do_something";
	this.template = "partials/governor.html";
	this.game = game;
	this.gameCallback = gameCallback;
	this.cardService = cardService;
	
	this.messages = {
		"intro" : {
			"discard" : {
				"c0" : "",
				"c1" : "You have too many hand cards and need to discard one. ",
				"c2" : "You have too many hand cards and need to discard %todiscard% cards. "
			},
			"chapel" : {
				"c0" : "As you have built a Chapel, you can choose to put a card under it for an extra victory point.",
				"c1" : "As you have built a Chapel, you can instead add this card to it for an extra victory point",
				"c2" : "As you have built a Chapel, one of these cards can be put under it for an extra victory point."
			}
		},
		"action" : {
			"chosen" : {
				"c0chapel" : "Choosing not to put a card under Chapel.",
				"start" : "Choosing ",
				"chapel" : "to put a %chapelCard% under your Chapel",
				"join" : " and choosing ",
				"discard" : "to discard %discardCards%",
				"end" : "."
			},
			"remain" : {
				"r0" : "",
				"r1" : "Still one card to select.",
				"r2" : "Still %remain% cards to select."
			}
		}
	};
	
	this.introductionMessage = this.buildIntroMessage();
	this.actionMessage = this.buildActionMessage();
};

GovernorPhaseResponse.prototype = {
	
	buildIntroMessage : function() {
		var discardKey = "c" + Math.min(2, this.options.numberOfCardsToDiscard);
		var message = this.messages.intro.discard[discardKey].replace("%todiscard%", this.options.numberOfCardsToDiscard);
		if (this.options.chapelOwner) {
			message += this.messages.intro.chapel[discardKey];
		}
		return message;
	},
	
	buildActionMessage : function() {
		var chapelOwner = this.options.chapelOwner;
		var chosenChapel = angular.isDefined(this.response.chapelCard);
		var chosenAnyCards = this.response.cardsToDiscard.length > 0;
		var chapelCard = chosenChapel ? 1 : 0; 
		var remaining = Math.max(0, this.options.numberOfCardsToDiscard - this.response.cardsToDiscard.length - chapelCard);
		var discardKey = "c" + Math.min(2, this.options.numberOfCardsToDiscard);
		var remainKey = "r" + Math.min(2, remaining);
		
		var messageArray = [];
		if (discardKey === "c0" && chapelOwner && !chosenChapel) {
			messageArray.push(this.messages.action.chosen.c0chapel);
		}
		if (chosenChapel || chosenAnyCards) {
			messageArray.push(this.messages.action.chosen.start);
		}
		if (chosenChapel) {
			messageArray.push(this.messages.action.chosen.chapel.replace("%chapelCard%", this.cardService.cardMap[this.response.chapelCard]));
		}
		if (chosenChapel && chosenAnyCards) {
			messageArray.push(this.messages.action.chosen.join);
		}
		if (chosenAnyCards) {
			messageArray.push(this.messages.action.chosen.discard.replace("%discardCards%", this.cardService.cardList(this.response.cardsToDiscard)));
		}
		if (chosenChapel || chosenAnyCards) {
			messageArray.push(this.messages.action.chosen.end);
			messageArray.push(" ");
			messageArray.push(this.messages.action.remain[remainKey].replace("%remain%", remaining));
		}
		return messageArray.join("");
	},
		
	sendResponse : function() {
		this.$xhr("PUT", "ws/games/" + this.game.gameId + "/rounds/" + this.game.roundNumber + "/governorChoice", this.response, this.gameCallback);
	},
	
	handCardSelectedType : function(handCard) {
		if (this.isDiscardCard(handCard)) {
			return "primary-selection";
		} else if (handCard === this.response.chapelCard) {
			return "secondary-selection";
		}
	},
	
	isDiscardCard : function(card) {
		return angular.Array.contains(this.response.cardsToDiscard, card);
	},
	
	clickHandCard : function(handCard) {
		
		if (this.response.chapelCard === handCard) {
			delete this.response.chapelCard;
			if (this.needToDiscardCards()) {
				this.response.cardsToDiscard.push(handCard);
			}
		} else if (this.isDiscardCard(handCard)) {
			angular.Array.remove(this.response.cardsToDiscard, handCard);
		} else {
			if (this.choicesMade()) {
				if (angular.isDefined(this.response.chapelCard)) {
					delete this.response.chapelCard;
				} else {
					this.response.cardsToDiscard.pop();
				}
			}
			if (this.options.chapelOwner && !angular.isDefined(this.response.chapelCard)) {
				this.response.chapelCard = handCard;
			} else {
				this.response.cardsToDiscard.push(handCard);
			}
		}
		this.actionMessage = this.buildActionMessage();
	},
	
	needToDiscardCards : function() {
		 return this.options.numberOfCardsToDiscard > 0;
	},
	
	choicesMade : function() {
		var cardsDiscarded = this.response.cardsToDiscard.length;
		if (this.needToDiscardCards() && angular.isDefined(this.response.chapelCard)) {
			cardsDiscarded += 1;
		}
		return cardsDiscarded === this.options.numberOfCardsToDiscard;
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
	this.prospected = game.$round.$phase.$play.offered.prospected;
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
	this.offeredCount = this.offered.councilOffered.length;
	this.keepCount = this.offered.councilRetainCount;
	this.cardsToDiscard = this.offeredCount - this.keepCount;
	this.template = "partials/councillor.html";
	this.mode = "do_something";
	this.game = game;
	this.gameCallback = gameCallback;
	this.calculateRemain();
};

CouncillorResponder.prototype = {
	
	commitResponse : function() {
		this.$xhr("PUT", "ws/games/" + this.game.gameId + "/rounds/" + this.game.roundNumber + "/phases/" + this.game.$round.phaseNumber + "/plays/" + this.game.$round.$phase.playNumber + "/decision", this.response, this.gameCallback);
	},
	
	calculateRemain : function() {
		this.remain = this.cardsToDiscard - this.response.councilDiscarded.length;
	},
	
	choicesMade : function() {
		return this.response.councilDiscarded.length === this.cardsToDiscard;
	},
	
	handCardSelectedType : function(handCard) {
		if (this.isDiscardCard(handCard)) {
			return "primary-selection";
		}
	},	
	
	councilCardSelectedType : function(councilCard) {
		if (this.isDiscardCard(councilCard)) {
			return "primary-selection";
		}
	},
	
	clickCouncilCard : function(councilCard) {
		if (this.isDiscardCard(councilCard)) {
			this.keepCard(councilCard);
		} else {
			this.addCardToDiscarded(councilCard);
		}
		this.calculateRemain();
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
		this.calculateRemain();
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
	this.response = { "payment" : [] };
	this.unableToBuildReason = undefined;
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
		return angular.isUndefined(this.unableToBuildReason) && angular.isDefined(this.response.build) && this.response.payment.length === this.currentBuildCost;
	},
	
	handCardSelectedType : function(handCard) {
		if (handCard === this.response.build) {
			return "secondary-selection";
		} else if (this.isPaymentCard(handCard)) {
			return "primary-selection";
		}
	},
	
	isPaymentCard : function(card) {
		return angular.Array.indexOf(this.response.payment, card) !== -1;
	},
	
	removePayment : function(card) {
		angular.Array.remove(this.response.payment, card);
	},
	
	clearToBuild : function() {
		delete this.response.build;
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
			if (angular.isUndefined(this.response.build)) {
				if (this.isPaymentCard(handCard)) {
					this.removePayment(handCard);
				}
				this.unableToBuildReason = this.offered.builderNotAllowedToBuild[handCard];
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
			return "primary-selection";
		}
	}
	
};

function TraderResponder($xhr, cardService, userManager, game, gameCallback) {
	this.$xhr = $xhr;
	this.cardService = cardService;
	this.userManager = userManager;
	this.goods = this.getPlayer(game, userManager.user.username).producedFactories
	;
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
		return angular.Array.contains(this.goods, card);
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
			return "primary-selection";
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
	this.confirmAbandonId = undefined;
	this.gamesInProgress = [];
	this.gamesCompleted = [];
	this.gamesAbandoned = [];
	this.gamesCreated = [];
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
		this.gamesInProgress = angular.Array.filter(response, { "state" : "PLAYING" });
		this.gamesCompleted = angular.Array.filter(response, { "state" : "COMPLETED" });
		this.gamesCreated = angular.Array.filter(response, { "state" : "RECRUITING" });
		this.gamesAbandoned = angular.Array.filter(response, { "state" : "ABANDONED" });
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
	
	otherPlayers : function(game) {
		var otherPlayerNames = [];
		var delimiter = "";
		var otherPlayerTextList = "";
		var authenticatedPlayer = this.userManager.user.username;
		angular.forEach(game.players, function(player) {
			if (player.name !== authenticatedPlayer) {
				otherPlayerNames.push(player.name);
			}
		});
		for (var i=0; i<otherPlayerNames.length; i++) {
		    var playerClass = game.currentPlayer === otherPlayerNames[i] ? "game-owner" : "";
			otherPlayerTextList += delimiter + "<strong class='" + playerClass + "' >" + otherPlayerNames[i] + "</strong>";
			delimiter = i === otherPlayerNames.length-2 ? " and " : ", ";
		}
		return otherPlayerTextList;
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
	
	requestQuit : function(game) {
		this.confirmAbandonId = game.gameId;
	},
	
	abandonGame : function(game) {
		var self = this;
		this.confirmAbandonId = undefined;
		this.$xhr("PUT", "ws/games/" + game.gameId + "/state", "ABANDONED", function(code, response) {
			self.refresh();
		});
	},
	
	deleteOrQuitGame : function(game, isDelete) {
		var self = this;
		this.confirmAbandonId = undefined;
		if (isDelete) {
			this.$xhr("DELETE", "ws/games/" + game.gameId, function(code, response) {
				self.refresh();
			});
		} else {
			this.$xhr("DELETE", "ws/games/" + game.gameId + "/players/" + this.userManager.user.username, function(code, response) {
				self.refresh();
			});				
		}
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
			this.users = response.users;
		},
		
		update : function(user) {
			var updatedUser = angular.Object.copy(user);
			var username = updatedUser.username;
			delete updatedUser.username;
			this.$xhr("PUT", "ws/users/" + user.username, updatedUser, this.getUsers);
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

function CardsController($xhr, cardService) {
	
	this.cardService = cardService;
	this.$xhr = $xhr;
	

};
CardsController.$inject = [ "$xhr", "cardService" ];
CardsController.prototype = {
	
};
