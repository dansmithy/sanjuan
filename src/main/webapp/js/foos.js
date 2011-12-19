function MainController($xhr) {
	
	var self = this;
	$xhr("JSON", "http://foosaholics.herokuapp.com/results?callback=JSON_CALLBACK", function(code, response) {
		self.results = response.results;
		self.augmentResults(self.results);
		self.playerList = self.createPlayerList(self.results).sort();
		self.selectedPlayer = self.playerList[0];
		self.highlighted = self.playerList[0];
	});
};

MainController.$inject = [ "$xhr" ];
MainController.prototype = {
		
	augmentResults : function(results) {
		var self = this;
		angular.forEach(results, function(result) {
			result.winningTeam = self.team(result, true);
			result.winningTeam.players = self.teamPlayers(result.winningTeam);
			result.losingTeam = self.team(result, false);
			result.losingTeam.players = self.teamPlayers(result.losingTeam);
			result.players = result.winningTeam.players.concat(result.losingTeam.players);
		});
	},
	
	createPlayerList : function(results) {
		var playerList = [];
		var self = this;
		angular.forEach(results, function(result) {
			angular.forEach(result.players, function(player) {
				if (!angular.Array.contains(playerList, player)) {
					playerList.push(player);
				}
			});
		});
		return playerList;
	},
	
	teamPlayers : function(team) {
		return [ team.defender, team.attacker ];
	},
	
	hasWon : function(result, player) {
		if (!angular.Array.contains(result.players, player)) {
			return "not-playing";
		};
		if (angular.Array.contains(result.winningTeam.players, player)) {
			return "winner";
		}
		return "loser";
	},
	
	team : function(result, winning) {
		return this.reverseBoolIfFalse(result.team1.score === 10, winning) ? result.team1 : result.team2;
	},
	
	reverseBoolIfFalse : function(bool, keepSame) {
		return keepSame ? bool : !bool; 
	}


};