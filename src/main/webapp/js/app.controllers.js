
function MainController($route, $xhr) {

	$xhr.defaults.headers.put["Content-Type"] = "application/json";
	$xhr.defaults.headers.post["Content-Type"] = "application/json";
	$xhr.defaults.headers.common["Content-Type"] = "application/json";

	$route.when("/games", { template : "partials/games.html", controller : GamesController });
	$route.when("/games/:gameId", { template : "partials/game.html", controller : GameController });
	$route.otherwise( { "redirectTo" : "/games" });
	$route.onChange(function() {
        $route.current.scope.params = $route.current.params;
    });
}

MainController.$inject = ["$route", "$xhr" ];

function GameController($xhr) {
	
	
	// modes = 
	//	awaiting_other_player, role_choice, governor_discard_choice, ...
	
	this.responder = { "mode" : "none" };
	
	this.role = "undecided";
	
	this.cardTypes = {};
	this.cardMap = {};
	
	this.roles = [ 
	                { "name" : "Governor" },
	                { "name" : "empty" },
	                { "name" : "Builder", "selectable" : true },
	                { "name" : "Trader", "selectable" : true },
	                { "name" : "Producer", "selectable" : true },
	                { "name" : "Councillor", "selectable" : true },
	                { "name" : "Prospector", "selectable" : true },
	                ];
	
	this.tariff = [ 1, 1, 2, 2, 3 ];
	
	this.user = "daniel";
	
//	this.players = [
//	  {   
//		  "name" : "Danny",
//		  "victory" : 12,
//		  "hand" : [ 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21 ],
//		  "buildings" : [ 20, 21, 22 ],
//		  "governor" : true
//	  },
//	  {
//		  "name" : "Dave",
//		  "victory" : 4,
//		  "buildings" : [ 23 ]
//	  }
//	                
//	];
	
	this.game = {};
	
	this.$xhr = $xhr;
	
	this.$xhr("GET", "ws/cards", this.firstCallback);

};
GameController.$inject = [ "$xhr" ];
GameController.prototype = {
		
		firstCallback : function(code, response) {
			this.cardMap = response;
			this.$xhr("GET", "ws/cards/types", this.secondCallback);
		},
		
		secondCallback : function(code, response) {
			this.cardTypes = response;
			this.$xhr("GET", "ws/games/" + this.params.gameId, this.gameCallback);
		},
		
		gameCallback : function(code, response) {
			this.processGame(response);
		},
		
		isSelf : function(playerName) {
			return playerName === this.user ? "self" : "";
		},
		
		cardImageUrl : function(id) {
			return "images/PlayingCards/" + this.cardMap[id] + ".BMP";
		},
		
		clickRoleCard : function(role) {
			if (this.responder.mode == "role_choice" && role.selectable) {
				this.responder.response.role = role.name;
			}
		},
		
		showSelected : function(role) {
			return this.responder.mode == "role_choice" && role.name == this.responder.response.role;
		},
		
		processGame : function(game) {
			game.$round = game.rounds[game.roundNumber-1];
			game.$round.$phase = game.$round.phases[game.$round.phaseNumber-1];
			if (game.$round.$phase.state === "AWAITING_ROLE_CHOICE") {
//				this.mode = "role_choice";
				this.responder = new GovernorChoiceResponse(this.$xhr, game, this.gameCallback);
			} else if (game.$round.$phase.state === "PLAYING") {
				this.responder = new DoSomethingResponder(this.$xhr, game, this.gameCallback);
			}
		},
		
		commitResponse : function() {
			this.responder.sendResponse();
		}
		
};

function GovernorChoiceResponse($xhr, game, gameCallback) {
	this.$xhr = $xhr;
	this.response = { "role" : "Builder" };
	this.mode = "role_choice";
	this.game = game;
	this.gameCallback = gameCallback;
};

GovernorChoiceResponse.prototype = {
	
	sendResponse : function(game) {
		this.$xhr("PUT", "ws/games/" + this.game.gameId + "/rounds/" + this.game.roundNumber + "/phases/" + this.game.$round.phaseNumber + "/type", this.response, this.gameCallback);
	}
};


function DoSomethingResponder($xhr, game, gameCallback) {
	this.$xhr = $xhr;
	this.response = "OK";
	this.mode = "do_something";
	this.game = game;
	this.gameCallback = gameCallback;
};

DoSomethingResponder.prototype = {
	
	sendResponse : function(game) {
		this.$xhr("GET", "ws/games/" + this.game.gameId + "/rounds/" + this.game.roundNumber + "/phases/" + this.game.$round.phaseNumber + "/plays/" + this.game.$round.$phase.playIndex, this.response);
	}
};


function GamesController($xhr, $location) {
	
	this.$xhr = $xhr;
	this.$location = $location;
	this.user = "daniel";
	this.games = [];
	this.refresh();
}

GamesController.$inject = [ "$xhr", "$location" ];

GamesController.prototype = {
	
	refresh : function() {
		this.$xhr("GET", "ws/games?player=" + this.user, this.gamesCallback);
	},
	
	gamesCallback : function(code, response) {
		this.games = response;
	},
	
	createGame : function() {
		var self = this;
		this.$xhr("POST", "ws/games", this.user, function(code, response) {
			self.refresh();
		});
	},
	
	addPlayer : function(game) {
		var self = this;
		this.$xhr("POST", "ws/games/" + game.gameId + "/players", game.playerToAdd, function(code, response) {
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