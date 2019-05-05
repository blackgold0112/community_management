/**
    入驻小区
**/
(function(vc){
    vc.extends({
        data:{
            roomInfo:{
                roomDataInfo:[],
            }
        },
        _initMethod:function(){

        },
        _initEvent:function(){
            vc.on('room','listRoom',function(_param){
                  vc.component.listRoom();
            });
            vc.on('room','loadData',function(_param){
                vc.component.listRoom(_param);
            });
        },
        methods:{
            listRoom:function(){
                var param = {
                    params:{
                        msg:this.message
                    }

               }
               //发送get请求
               vc.http.get('room',
                            'listRoom',
                             param,
                             function(json,res){
                                vc.component.roomInfo.roomDataInfo=JSON.parse(json);
                             },function(errInfo,error){
                                console.log('请求失败处理');
                             }
                           );
            },
            _openAddRoomModal:function(){
                //vc.emit('storeEnterCommunity','openStoreEnterCommunity',{});
            },
            _openDelRoomModel:function(_community){
                //vc.emit('storeExitCommunity','openStoreExitCommunityModal',_community);
            }
        }
    });
})(window.vc);