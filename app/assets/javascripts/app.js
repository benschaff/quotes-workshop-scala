var webjars = {
    versions: {
        'requirejs': '2.1.14-3',
        'jquery': '1.11.1',
        'angularjs': '1.2.25',
        'bootstrap': '3.2.0',
        'highcharts': '4.0.3-1',
        'angular-loading-bar': '0.5.1'
    },
    path: function(webjarid, path) {
        return '/webjars/' + webjarid + '/' + webjars.versions[webjarid] + '/' + path;
    }
};

define('webjars', function () {
    return { load: function (name, req, onload, config) { onload(); } }
});

requirejs.config({
	paths: {
        'jquery': webjars.path("jquery", "jquery"),
        'angular': webjars.path("angularjs", "angular"),
        'angular-route': webjars.path("angularjs", "angular-route"),
        'angular-animate': webjars.path("angularjs", "angular-animate"),
        'angular-resource': webjars.path("angularjs", "angular-resource"),
        'bootstrap': webjars.path("bootstrap", "js/bootstrap"),
        'highcharts': webjars.path("highcharts", "highcharts")
    },
	shim : {
        'jquery': { "exports": "$" },
        'angular': { deps: [ 'jquery' ], "exports": "angular" },
        'angular-route': { deps: [ 'angular' ] },
        'angular-animate': { deps: [ 'angular' ] },
        'angular-resource': { deps: [ 'angular' ] },
        'bootstrap': { deps: [ 'jquery' ], "exports": "bootstrap" },
        'highcharts': { deps: [ 'jquery' ] }
	}
});

require(['angular'], function(angular) {
    angular.module('app.controllers', []);
});

require(
    [
        'angular',
        'angular-route',
        'angular-animate',
        'angular-resource',
        'jquery',
        'bootstrap',
        'highcharts',
        './controller/MainController',
        './controller/SymbolsController',
        './service/ChartingService',
        './service/SymbolsService',
        './service/VCardService'
    ],
	function (angular) {
        angular.module('app',
            [
                'ngResource',
                'ngRoute',
                'ngAnimate',
                'app.controllers',
                'ChartingService',
                'SymbolsService',
                'VCardService'
            ]
        ).config(['$routeProvider', function($routeProvider) {
			$routeProvider.when('/', {
                templateUrl: 'assets/javascripts/view/main.html',
                controller: 'MainController',
                resolve: {}
            }).when('/symbols', {
                templateUrl: 'assets/javascripts/view/symbols.html',
                controller: 'SymbolsController'
            }).otherwise({ redirectTo: '/' });
		}]).run(function($rootScope) {
            $rootScope.dismissMessage = function() {
                $rootScope.messages = [];

                $rootScope.$root.$eval();
            };

            $rootScope.setMessage = function(message) {
                $rootScope.messages = [ message ];

                $rootScope.$root.$eval();
            };
        // From https://github.com/rootux/angular-highcharts-directive/blob/master/src/directives/highchart.js
        }).directive('chart', function () {
            return {
                restrict: 'E',
                template: '<div></div>',
                scope: {
                    chartData: "=value"
                },
                transclude: true,
                replace: true,

                link: function (scope, element, attrs) {
                    var chartsDefaults = {
                        chart: {
                            renderTo: element[0],
                            type: attrs.type || null,
                            height: attrs.height || null,
                            width: attrs.width || null
                        }
                    };

                    //Update when charts data changes
                    scope.$watch(function() { return scope.chartData; }, function(value) {
                        if(!value) return;
                        // We need deep copy in order to NOT override original chart object.
                        // This allows us to override chart data member and still the keep
                        // our original renderTo will be the same
                        var deepCopy = true;
                        var newSettings = {};
                        $.extend(deepCopy, newSettings, chartsDefaults, scope.chartData);
                        new Highcharts.Chart(newSettings);
                    });
                }
            };
        });
		
		angular.bootstrap(document, ['app']);
	}
);