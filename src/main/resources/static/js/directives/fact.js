angular.module('flow').directive('fact', ['$timeout', function ($timeout) {
    var printString = function(fact) {
        return "<div class='value'>" + (typeof fact != "undefined" ? fact : 'null') + "</div>"
    }
    var printObject = function(fact) {
        var html = ""
        var keys = Object.keys(fact)
        for (var i = 0; i < keys.length; i++) {
            if (keys[i] == '$$hashKey') continue

            var value = fact[keys[i]]
            if (typeof value != "undefined") {
                if (typeof value === 'object') {
                    html += "<div class='fact'>{<div class='key'>" + keys[i] + ":</div>" + printObject(value) + "}</div>"
                } else {
                    html += "<div class='fact'><div class='key'>" + keys[i] + ":</div>" + printString(value) + "</div>"
                }
            }
        }
        return html == "" ? null : html
    }
    return {
        link: function($scope, element, attrs) {
            var fact = $scope.fact
            element.html(printObject(fact));
        },
        templateUrl: 'view/fact.html'
    };
}])