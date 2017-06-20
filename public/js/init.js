if(window.WebSocket){
    console.log('This browser supports WebSocket');
}else{
    console.log('This browser does not supports WebSocket');
}
var socket=io.connect();
$(document).ready(function(){
    var chatApp=new Chat(socket);
    socket.on('nameResult',function(result){
        var message;
        if(result.success){
            message='You are known as '+result.name+'.';
        }else{
            message=result.message;
        }
        console.log("nameResult:---"+message);
        $('#messages').append(divSystemContentElement(message));
        $('#nickName').text(result.name);
    });

    socket.on('joinResult',function(result){
        console.log('joinResult:---'+result);
        $('#room').text(result.room);
        $('#messages').append(divSystemContentElement('Room changed.'));
    });

    socket.on('message',function(message){
        console.log('message:---'+message);
        var newElement=$('<div></div>').text(message.text);
        $('#messages').append(newElement);
        $('#messages').scrollTop($('#messages').prop('scrollHeight'));
    });

    socket.on('rooms',function(rooms){
        console.log('rooms:---'+rooms);
        rooms=JSON.parse(rooms);
        $('#room-list').empty();
        for(var room in rooms){
            $('#room-list').append(divEscapedContentElement(room+':'+rooms[room]));
        }
        $('#room-list div').click(function(){
            chatApp.processCommand('/join '+$(this).text().split(':')[0]);
            $('#send-message').focus();
        });
    });

    setInterval(function(){
        socket.emit('rooms');
    },1000);

    $('#send-message').focus();
    $('#send-button').click(function(){
        processUserInput(chatApp,socket);
        $('#send-message').focus();
    });
});