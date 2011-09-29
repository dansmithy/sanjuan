
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
