var vertx = require('vertx')
var console = require('vertx/console')

// vertx.eventBus.registerHandler('ping-address', function(message, replier) {
//   replier('pong!');
//   console.log('Sent back pong JavaScript!')
// });

var count = 0;

vertx.setPeriodic(1000, function(id) {
    console.log('In event handler ' + count); 
    count++;
    if (count === 10) {
        vertx.cancelTimer(id);
    }
});