angular.module("flow", [
    'ngRoute',
    'ui.router',
    'ngSanitize',
    'ui.bootstrap',
    'chart.js'
])
// configure our routes
angular.module("flow").config(function ($routeProvider, $stateProvider, $urlRouterProvider) {

    $urlRouterProvider.otherwise("/")

    $stateProvider
        .state('index', {
            url: "/",
            views: {
                "single": {
                    templateUrl: 'view/welcome.html',
                    controller: welcomeController,
                    resolve: welcomeController.resolve
                }
            }
        })
        .state('conversation', {
            url: "/conversation/:conversation",
            views: {
                "single": {
                    templateUrl: 'view/conversation.html',
                    controller: conversationController,
                    resolve: conversationController.resolve
                }
            }
        })

})
angular.module("flow").run(function ($rootScope) {

})

angular.module("flow").controller('mainController', function ($scope) {

})