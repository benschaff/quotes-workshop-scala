define(['angular'], function(angular) {

	angular.module('VCardService', ['ngResource']).factory('VCardService', function($http) {
	    var url = decodeURIComponent(wikipediaApiJavascriptRoutes.controllers.WikipediaApi.vcard().absoluteURL());
	
	    return function(data) {
            return $http.post(url, data);
        };
	});

});
