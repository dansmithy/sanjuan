

/*
 * Filters
 * 
 */

angular.filter("null", function(condition, valueIfNull, valueIfNotNull) {
	return condition == null ? valueIfNull : valueIfNotNull;
});

angular.filter("defined", function(value, valueIfDefined, valueIfUndefined) {
	return angular.isDefined(value) ? valueIfDefined : valueIfUndefined;
});

angular.filter("undefined", function(value, valueIfUndefined) {
	return angular.isDefined(value) ? value : valueIfUndefined;
});

angular.filter("repeated", function(count, char) {
	return Array(count).join(char);
});

angular.filter("ternary", function(condition, valueIfTrue, valueIfFalse, valueIfUndefined) {
	if (!angular.isDefined(condition)) {
		return valueIfUndefined || '';
	}
	return condition ? valueIfTrue : valueIfFalse;
});

angular.filter("join", function(array, delimiter, finalDelimiter) {
	var arrayCopy = angular.Object.copy(array);
	var lastItem = arrayCopy.pop();
	var strList = arrayCopy.join(delimiter);
	if (arrayCopy.length >= 1) {
		strList += finalDelimiter;
	}
	strList += lastItem;
	return strList;
});

angular.filter("prettyDate", function(time) {
	
	var prettyDate = function(date){
		var diff = (((new Date()).getTime() - date.getTime()) / 1000),
		day_diff = Math.floor(diff / 86400);
				
		if ( isNaN(day_diff) || day_diff < 0 || day_diff >= 31 )
			return;
				
		return day_diff == 0 && (
				diff < 60 && "just now" ||
				diff < 120 && "1 minute ago" ||
				diff < 3600 && Math.floor( diff / 60 ) + " minutes ago" ||
				diff < 7200 && "1 hour ago" ||
				diff < 86400 && Math.floor( diff / 3600 ) + " hours ago") ||
			day_diff == 1 && "Yesterday" ||
			day_diff < 7 && day_diff + " days ago" ||
			day_diff < 31 && Math.ceil( day_diff / 7 ) + " weeks ago";
	};

	if (time) {
		return prettyDate(time);
	} else {
		return "";
	}
});

