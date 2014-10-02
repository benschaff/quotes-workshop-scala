define(['angular'], function(angular) {

	angular.module('SymbolsService', ['ngResource']).factory('SymbolsService', function($resource) {
	    var url = decodeURIComponent(stockApiJavascriptRoutes.controllers.StockApi.symbols(':query').absoluteURL());
	    url = url.replace(":9000", "\\:9000");
	
	    return $resource(url, {});
	});

});
