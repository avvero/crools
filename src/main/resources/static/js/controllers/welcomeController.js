function welcomeController($scope, $http, feature) {
    $scope.feature = feature
    $scope.groupStatsLabels = []
    $scope.groupStatsData = []
    //view
    $scope.analyseTab = 'checkList'
    $scope.showEntriesInNullGroup = false
    $scope.showTotalDistributionsGtVariants = false
    //

    $scope.data = {
        expressions: []
    }
    $scope.analyse = function (data) {
        $http({
            method: 'POST',
            url: '/api/analise',
            // headers: {'Content-Type': 'application/octet-stream;charset=UTF-8'},
            data: data
        })
            .success(function (data) {
                $scope.data = data
                $scope.collectStatistics(data)
                $scope.analyseTextPhrases($scope.feature.text)
                $scope.drawGraphics(data)
            })
            .error(function (error) {
//                console.error(error)
                $scope.analyseTextPhrases($scope.feature.text)
                if (error.message.indexOf("You must implement missing steps with") >= 0) {
                    $scope.statistic = {
                        errors: []
                    }
                    var text = $scope.textToPhraseList($scope.feature.text)

                    for (var i = 0; i < $scope.data.expressions.length; i++) {
                        var re = new RegExp($scope.data.expressions[i].value, 'gm')
                        text = text.replace(re, '');
                    }
                    text = text.replace(/^\n/gm, '');
                    $scope.statistic.errors.push({name: "analiseError", value: text})
                } else if (error.message.indexOf("Error parsing feature file") >= 0){
                    $scope.statistic.errors.push({name: "featureParsingError", value: error.message})
                } else {
                    $scope.statistic.errors.push({name: "unexpectedError", value: error.message})
                }
            });
    }
    $scope.textToPhraseList = function(text) {
        text = text.replace(/Feature.*/g, '');
        text = text.replace(/.*?Scenario.*/g, '');
        text = text.replace(/.*\|.*\|.*/g, '');

        text = text.replace(/When\s*/g, '');
        text = text.replace(/Then\s*/g, '');
        text = text.replace(/Given\s*/g, '');
        text = text.replace(/But\s*/g, '');
        text = text.replace(/And\s*/g, '');
        text = text.replace(/^(\s)*/gm, '');
        text = text.replace(/^(\n)*/gm, '');
        return text
    }
    $scope.collectStatistics = function (data) {
        $scope.statistic = {
            errors: []
        }
        var totalDistributions = 0
        for (var i = 0; i < data.groupStats.length; i++) {
            var groupStat = data.groupStats[i]
            totalDistributions += groupStat.count
            if (groupStat.name == null && groupStat.count > 0) {
                $scope.statistic.errors.push({name: "entriesInNullGroup", value: groupStat.count})
            }
        }
        if (totalDistributions > data.distributions.length) {
            $scope.statistic.errors.push({name: "totalDistributionsGtVariants", value: totalDistributions})
        }
    }
    $scope.analyseTextPhrases = function (text) {
        var text = $scope.textToPhraseList(text)
        for (var i = 0; i < $scope.data.expressions.length; i++) {
            $scope.data.expressions[i].usage = 0
            var re = new RegExp($scope.data.expressions[i].value, 'gm')
            if (text != text.replace(re, '')) {
                $scope.data.expressions[i].usage = 1
            }
        }
    }
    $scope.drawGraphics = function (data) {
        $scope.groupStatsLabels = []
        $scope.groupStatsData = []
        for (var i = 0; i < data.groupStats.length; i++) {
            $scope.groupStatsLabels.push(data.groupStats[i].name)
            $scope.groupStatsData.push(data.groupStats[i].count)
        }
    }

    // init
    $scope.analyse($scope.feature.text)
}

welcomeController.resolve = {
    feature: function ($q, $http, $stateParams) {
        var deferred = $q.defer();

        $http({
            method: 'GET',
            url: 'api/feature',
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        })
            .success(function (feature) {
                deferred.resolve(feature)
            })
            .error(function (data) {
                deferred.reject("error value");
            });

        return deferred.promise;
    }
}