define(['angular'], function(angular) {

    angular.module('app.controllers').controller('MainController', [ '$rootScope', '$scope', '$filter', '$sce', 'Last30DaysService', 'VCardService', function($rootScope, $scope, $filter, $sce, Last30DaysService, VCardService) {
        $rootScope.action = 'home';

        $scope.demo = {
            symbols: [
                { 'Name': 'Google Inc', 'Symbol': 'GOOG' },
                { 'Name': 'Oracle Corp', 'Symbol': 'ORCL' }
            ]
        };

        $scope.changeSymbol = function(symbol) {
            Last30DaysService.get(
                { symbol: symbol.Symbol },
                function(chartData) {
                    var categories = [];
                    $.each(chartData.Dates, function(index, date) {
                        categories.push($filter('date')(date, 'dd/MM'));
                    });

                    $scope.currentSymbolChartData = {
                        "title": {
                            "text": "Price history of " + symbol.name
                        },
                        "subtitle": {
                            "text": "30 days period"
                        },
                        "xAxis": {
                            "categories": categories
                        },
                        "tooltip": {},
                        "plotOptions": {
                            "area": {
                                "pointStart": chartData.Elements[0].DataSeries.close.values[0]
                            }
                        },
                        "series": [
                            {
                                "name": "Close price",
                                "data": chartData.Elements[0].DataSeries.close.values
                            }
                        ]
                    };

                    $scope.currentSymbol = symbol;
                },
                function(error) {
                    console.log(error);

                    $rootScope.setMessage({ type: 'error', text: 'An error occurred. Please try again later' });
                }
            );

            VCardService(symbol).success(function(html) {
                var $wikiDOM = $("<document>" + html + "</document>");

                $scope.vcard = $sce.trustAsHtml($wikiDOM.find('.infobox').html());
            }).error(function(error) {
                console.log(error);

                $rootScope.setMessage({ type: 'error', text: 'An error occurred. Please try again later' });
            });
        };

        $scope.changeSymbol($scope.demo.symbols[0]);
    }]);

});
