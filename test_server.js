var io = require('socket.io').listen(8000);

// open the socket connection
io.sockets.on('connection', function (socket) {
  //Recode user online
  var sender;
  socket.on('user-online',function(data){
    var obj = JSON.parse(data);
    sender = obj.sender;
    socket.broadcast.emit('online',{sender : sender});
  });

	socket.on('message', function(message){
    var obj = JSON.parse(message);
		socket.broadcast.emit('event',{sender : obj.sender, message : obj.message});
    console.log(message);
	});

  socket.on('disconnect', function (socket){
    socket.broadcast.emit('offline',{sender : sender})
  });

});
