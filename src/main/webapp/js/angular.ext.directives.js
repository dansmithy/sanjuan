

//angular.widget("ng:nav", function(compileElement) {
//  this.descend(true);
//  this.directives(true);
//  compileElement.add
//});

angular.directive("ng:clickstop", function(expression, element){
	  return angular.extend(function($updateView, element){
	    var self = this;
	    element.bind('click', function(event){
	      self.$tryEval(expression, element);
	      $updateView();
	      event.stopPropagation();
	      return false;
	    });
	  }, {$inject:['$updateView']});
	});


angular.directive("ng:intercept", function(expression, templateElement) {
	  return angular.extend(function($location, $updateView, element) {
		var self = this;
		var href = angular.fromJson(element.attr("ng:bind-attr")).href;
		href = href.substring(2, href.length-2);
	    element.bind('click', function(event) {
	    	  var newhash = self.$eval(href, element).substring(1);
		      event.stopPropagation();
		      var message = self.$eval(expression);
		      if (message) {
		    	  $( "#dialog-confirm" ).dialog({
		    		  resizable: false,
		    		  height:140,
		    		  modal: true,
		    		  buttons: {
		    			  Yes : function() {
		    				  $( this ).dialog( "close" );
		    				  $location.hash = newhash;
		    				  $updateView();
		    			  },
		    			  No: function() {
		    				  $( this ).dialog( "close" );
		    			  }
		    		  }
		    	  });
		    	  return false;
		      } else {
		    	  return true;
		      }
	    });
	  }, {$inject:['$location', '$updateView']});
	});

angular.directive("ng:show-fade", function(expression, compileElement) {
	this.descend(true);
	return function(element) {
		element.hide();
		this.$watch(expression, function(value) {
			if (value) {
				element.fadeIn();	
			} else {
				element.fadeOut();
			}
		});
	};
});

angular.directive("ng:readonly", function(expression, compileElement) {
	this.descend(true);
	return function(element) {
		this.$watch(expression, function(value) {
			if (value) {
				element.attr("readonly", "true");
				element.focus(function(event) {
					event.target.blur();
				});
			} else {
				element.removeAttr("readonly");
				element.unbind("focus");
			}
		});
	};
});

angular.directive('ng:highlight', function(expression) {
	  return function(element) {
		  var currentScope = this;
		  currentScope.$watch(expression, function(value) {
			  if (value) {
				  element.stop(true, true);
				  element.effect("highlight", {}, 3000);
			  }
		  });
	  };
});

angular.widget('my:button', function(compileElement) {
	  this.descend(true);
//	  compileElement.prepend("<test>");
//	  compileElement.append("</test>");
	  var html = compileElement.html();
	  compileElement.html("<div ng:hide='showValue'>" + html + "</div>");
	  return function(linkElement) {
	    var currentScope = this;
	    
	  };
	});

angular.directive('ng:pressreturn', function(expression) {
	  var compiler = this;
	  return function(textboxElement) {
		  var currentScope = this;
	      textboxElement.keyup(function (event) {
	    	if (event.keyCode == 13) {
	    		currentScope.$eval(expression);
	    	}
	    });
	    
	  };
});

angular.directive('ng:disabled', function(expression) {
	  var compiler = this;
	  return function(element) {
		  var currentScope = this;
		  currentScope.$watch(expression, function(value) {
			 var attrValue = value ? 'disabled' : '';
			 element.attr('disabled', attrValue); 
		  });
	  };
});

angular.directive('ng:barchart', function(expression) {
	  var compiler = this;
	  return function(element) {
		  var currentScope = this;
		  currentScope.$watch(expression, function(value) {
		        var data = new google.visualization.DataTable();
		        data.addColumn('string', 'Year');
		        data.addColumn('number', 'Population');
		        data.addRows(value.length);
		        var index = 0;
		        angular.forEach(angular.Array.orderBy(value, "year"), function(yearItem) {
		        	data.setValue(index, 0, ""+yearItem.year);
		        	data.setValue(index, 1, yearItem.population);
		        	index++;
		        });
		        var chart = new google.visualization.ColumnChart(element.get(0));
		        chart.draw(data, {width: 400, height: 240, title: 'Country Population',
		                          hAxis: {title: 'Year', titleTextStyle: {color: 'red'}}
		                         });			  
		  });
	  };
});

angular.directive('ng:worldmap', function(expression) {
	  var compiler = this;
	  return function(element) {
		  var currentScope = this;
		  currentScope.$watch(expression, function(value) {
		        var data = new google.visualization.DataTable();
		        data.addRows(1);
		        data.addColumn('string', 'Country');
		        data.addColumn('number', 'Selected');
		        data.setValue(0, 0, value);
		        data.setValue(0, 1, 200);
		        var geochart = new google.visualization.GeoChart(element.get(0));
		        geochart.draw(data, {});
		  });
	  };
});


angular.directive("ng:twipsy", function() {
	return function(element) {
		element.twipsy();
	};
});