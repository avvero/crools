function welcomeController($scope, $http, feature) {
    $scope.feature = feature

    $scope.data = {}
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
            })
            .error(function (error) {
                console.error(error)
            });
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
        if (totalDistributions > data.variants) {
            $scope.statistic.errors.push({name: "totalDistributionsGtVariants", value: totalDistributions})
        }
    }
}

welcomeController.resolve = {
    feature: function ($q, $http, $stateParams) {
        var deferred = $q.defer();

        $http({
            method: 'GET',
            url: '/api/feature',
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

welcomeController.resolve2 = {
    data: function ($q, $http, $stateParams) {
        var deferred = $q.defer();

        $http({
            method: 'GET',
            url: '/api/analise',
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        })
            .success(function (data) {
                deferred.resolve(data)
            })
            .error(function (data) {
                deferred.reject("error value");
            });

        return deferred.promise;
    }
}