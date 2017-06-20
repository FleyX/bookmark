const 
    socketio=require('socket.io');

var io,
    guestNumber=1,  //用户编号
    nickNames={},   //socket id对应的nickname
    namesUsed={},   //所有已使用的nickname
    allRooms={},    //聊天室--人数
    currentRoom={}; //sockid--聊天室

module.exports.listen=function(server){
    io=socketio.listen(server);
    io.serveClient('log level',1);
    io.sockets.on('connection',function(socket){
        guestNumber=assignGuestName(socket,guestNumber,nickNames);
        joinRoom(socket,'Lobby');
        handleMessageBroadcasting(socket,nickNames);
        handleNameChangeAttempts(socket,nickNames,namesUsed);
        handleRoomJoining(socket);
        socket.on('rooms',function(){
            socket.emit('rooms',JSON.stringify(allRooms));
        });
        handleClientDisconnection(socket,nickNames,namesUsed);
    });
};
//新socket连入，自动分配一个昵称
function assignGuestName(socket,guesetNumber,nickNames){
    var name='Guest'+guestNumber;
    nickNames[socket.id]=name;
    socket.emit('nameResult',{
        success:true,
        name:name
    });
    namesUsed[name]=1;
    return guestNumber+1;
}
//加入某个聊天室
function joinRoom(socket,room){
    socket.join(room);
    var num=allRooms[room];
    if(num===undefined){
        allRooms[room]=1;
    }else{
        allRooms[room]=num+1;
    }
    currentRoom[socket.id]=room;
    socket.emit('joinResult',{room:room});
    socket.broadcast.to(room).emit('message',{
        text:nickNames[socket.id]+' has join '+room+'.'
    });

    var usersinRoom=io.sockets.adapter.rooms[room];
    if(usersinRoom.length>1){
        var usersInRoomSummary='Users currently in '+room+' : ';
        for(var index in usersinRoom.sockets){
            if(index!=socket.id){
                usersInRoomSummary+=nickNames[index]+',';
            }
        }
        socket.emit('message',{text:usersInRoomSummary}); 
    }
}
//修改昵称
function handleNameChangeAttempts(socket,nickNames,namesUsed){
    socket.on('nameAttempt',function(name){
        if(name.indexOf('Guest')==0){
            socket.emit('nameResult',{
                success:false,
                message:'Names cannot begin with "Guest".'
            });
        }else{
            if(namesUsed[name]==undefined){
                var previousName=nickNames[socket.id];
                delete namesUsed[previousName];
                namesUsed[name]=1;
                nickNames[socket.id]=name;
                socket.emit('nameResult',{
                    success:true,
                    name:name
                });
                socket.broadcast.to(currentRoom[socket.id]).emit('message',{
                    text:previousName+' is now known as '+name+'.'
                });
            }else{
                socket.emit('nameResult',{
                    success:false,
                    message:'That name is already in use.'  
                });
            }
        }
    });                                                                        
}
//将某个用户的消息广播到同聊天室下的其他用户
function handleMessageBroadcasting(socket){
    socket.on('message',function(message){
        console.log('message:---'+JSON.stringify(message));
        socket.broadcast.to(message.room).emit('message',{
            text:nickNames[socket.id]+ ': '+message.text
        });
    });
}
//加入/创建某个聊天室
function handleRoomJoining(socket){
    socket.on('join',function(room){
        var temp=currentRoom[socket.id];
        delete currentRoom[socket.id];
        socket.leave(temp);
        var num=--allRooms[temp];
        if(num==0)
            delete allRooms[temp];
        joinRoom(socket,room.newRoom);
    });
}
//socket断线处理
function handleClientDisconnection(socket){
    socket.on('disconnect',function(){
        console.log("xxxx disconnect");
        allRooms[currentRoom[socket.id]]--;
        delete namesUsed[nickNames[socket.id]];
        delete nickNames[socket.id];
        delete currentRoom[socket.id];
    })
}