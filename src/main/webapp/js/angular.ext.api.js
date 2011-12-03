/**
 * These functions are generally useful functions that extend the core angular API 
 */


angular.parameterize = function(path, params) {
	params = params || {};
    angular.forEach(params, function(paramValue, paramName){
        path = path.replace(new RegExp(":" + paramName + "(\\W){0,1}"), paramValue + "$1");
    });
    return path;
};

angular.Array.get = function(array, matchingItem, fieldToMatchOn) {
	var foundItem = false;
	angular.forEach(array, function(arrayItem) {
		if (fieldToMatchOn) {
			if (arrayItem[fieldToMatchOn] === matchingItem[fieldToMatchOn]) {
				foundItem = arrayItem;
			}
		} else if (arrayItem === matchingItem) {
			foundItem = arrayItem;
		}
	});
	return foundItem;	
};

angular.Array.containsObject = function(array, matchingItem, fieldToMatchOn) {
	return angular.Array.get(array, matchingItem, fieldToMatchOn) == false ? false : true;
};

angular.Array.contains = function(array, matchingItem) {
	return angular.Array.indexOf(array, matchingItem) != -1;
};