define(['angular'], function(angular) {

	angular.module('ChartingService', ['ngResource']).factory('Last30DaysService', function($resource) {
	    var url = decodeURIComponent(stockApiJavascriptRoutes.controllers.StockApi.last30Days(':symbol').absoluteURL());
	    url = url.replace(":9000", "\\:9000");
	
	    return $resource(url, {});
	});

});
