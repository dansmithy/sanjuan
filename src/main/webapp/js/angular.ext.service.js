angular.service('$xhr.error', function(){
	  return function(request, response){
		  request.callback(response.status, response.body);
	  };
	}, {
		$eager : 'true'
	});

angular.service("userManager", function() {
	return {
		
		credentials : {},
		user : undefined,
		state : "verifying", // also unauthenticated, authenticated, authenticating
		error : undefined,
		
		authenticated : function(newUser) {
			this.user = newUser;
			this.state = "authenticated";
			this.credentials.password = undefined;
		},
		
		authenticating : function() {
			this.error = undefined;
			this.state = "authenticating";
		},
		
		unauthenticated : function(error) {
			if (error) {
				this.error = error;
			}
			this.user = undefined;
			this.state = "unauthenticated";
		},
		
		isAuthenticated : function() {
			return this.state == "authenticated";
		},
		
		isUnauthenticated : function() {
			return this.state == "unauthenticated";
		},
		
		isAuthenticating : function() {
			return this.state == "authenticating";
		},
		
		isVerifying : function() {
			return this.state == "verifying";
		},
		
		isVerifiedButUnauthenticated : function() {
			return this.isUnauthenticated() || this.isAuthenticating();
		},
		
		isProcessing : function() {
			return this.isAuthenticating() || this.isVerifying();
		},
		
		hasErrors : function() {
			return angular.isDefined(this.error);
		}
		
	};
	
});



angular.service("pipelineXhr", angular.extend(function($xhr, userManager) {
	$xhr.defaults.headers.common["Content-Type"] = "application/json";
	$xhr.defaults.headers.post["Content-Type"] = "application/json";
	
	return {
		
		call : function(type, url, bodyOrCallback, callback) {
			var self = this;
			var callbackfn = callback || bodyOrCallback || angular.noop;
			var authenticationCallback = self.createAuthenticationCallback(callbackfn, url);
			if (type === "POST" || type === "PUT" || type === "DELETE") {
				$xhr(type, url, bodyOrCallback, authenticationCallback);
			} else {
				$xhr(type, url, authenticationCallback);
			}
		},
		
		/**
		 * Unknown to the UI JS, the user may have been unauthenticated (eg. by a period of inactivity or by explicitly logging out in another browser tab).
		 * This code will handle the 403 response and update the UI authenticated status. 
		 * 
		 * @param callbackfn
		 * @param url
		 * @returns
		 */
		createAuthenticationCallback : function(callbackfn, url) {
			return function(code, response) {
				if (code === 403 && url !== "j_spring_security_check") {
					userManager.unauthenticated("You are no longer logged in.");
				} else {
					callbackfn(code, response);
				}
			};
		}
		
	};
}, {$inject:["$xhr", "userManager" ]}));

angular.service("cardService", angular.extend(function($xhr) {
	
	var cardService = {

		cardTypes : {},
		cardMap : {},
		
		cardImageUrl : function(id) {
			return cardService.typeImageUrl(cardService.cardMap[id]);
		},
		
		typeImageUrl : function(type) {
			return "images/PlayingCards/" + type + ".BMP";
		},
		
		cardType : function(id) {
			return cardService.cardTypes[cardService.cardMap[id]];
		},
		
		isReady : function() {
			return !angular.Object.equals({}, cardService.cardMap) && !angular.Object.equals({}, cardService.cardTypes); 
		},
		
		cardMapCallback : function(code, response) {
			cardService.cardMap = response;
		},
		
		cardTypesCallback : function(code, response) {
			cardService.cardTypes = response;
		},
		
		isChapel : function(id) {
			return cardService.cardMap[id] === "Chapel";
		},

		isLibrary : function(id) {
			return cardService.cardMap[id] === "Library";
		},

		cardList : function(cardArray) {
			var arrayCopy = angular.Object.copy(cardArray);
			var nameArray = [];
			for (var i=0; i<cardArray.length; i++) {
				nameArray.push(this.cardMap[cardArray[i]]);
			}
			var lastItem = nameArray.pop();
			var strList = nameArray.join(", ");
			if (nameArray.length >= 1) {
				strList += " and ";
			}
			strList += lastItem;
			return strList;
			
		}
		

	};
	
	$xhr("GET", "ws/cards", cardService.cardMapCallback);
	$xhr("GET", "ws/cards/types", cardService.cardTypesCallback);

	return cardService;
	
}, {$inject:["$xhr" ]}));




angular.service("gameService", angular.extend(function($xhr, $defer, $log) {
	
	var $defer = $defer;
	var gameService = {
		
		doGameUpdates : false,
		gameId : -1,
		version : -1,
		gameCallback : angular.noop,
		requestTrack : 0,
		
		poll : function() {
			$defer(gameService.actionPoll, 2000);
		},
		
		startGameUpdates : function(gameId, version, gameCallback) {
			gameService.requestTrack = 0;
			if (angular.isDefined(gameId)) {
				gameService.doGameUpdates = true;
				gameService.gameId = gameId;
				gameService.version = version;
				gameService.gameCallback = gameCallback;
			} else {
			}
		},
		
		stopGameUpdates : function() {
			gameService.doGameUpdates = false;
			gameService.requestTrack = 0;
			gameService.version = -1;
		},
			
		updateGame : function(gameId, version, gameCallback) {
			if (gameService.requestTrack < 2) {
				gameService.requestTrack += 1;
				$xhr("GET", "ws/games/" + gameId, function(code, response) {
					if (gameService.requestTrack > 0) {
					    gameService.requestTrack -= 1;
					}
					if (response.version !== version) {
						gameCallback(code, response);
					}
				});
			}
		},
		
		actionPoll : function() {
			if (gameService.doGameUpdates) {
				gameService.updateGame(gameService.gameId, gameService.version, gameService.gameCallback);
			}
			gameService.poll();
		}
		
	};
	
	gameService.poll();
	return gameService;
}, {$inject:["$xhr", "$defer", "$log" ]}));

