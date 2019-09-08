(function(vc){
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data:{
            listFloorInfo:{
                floors:[],
                total:0,
                records:1,
                errorInfo:"",
                conditions:{
                    floorId:'',
                    floorNum:'',
                    floorName:''
                }
            }
        },
        _initMethod:function(){
            vc.component._listFloorData(DEFAULT_PAGE,DEFAULT_ROWS);
        },
        _initEvent:function(){
            vc.on('listFloor','listFloorData',function(){
                vc.component._listFloorData(DEFAULT_PAGE,DEFAULT_ROWS);
            });
            vc.on('pagination','page_event',function(_currentPage){
                vc.component._listFloorData(_currentPage,DEFAULT_ROWS);
            });
        },
        methods:{
            _listFloorData:function(_page,_rows){

                vc.component.listFloorInfo.conditions.page = _page;
                vc.component.listFloorInfo.conditions.row = _rows;
                vc.component.listFloorInfo.conditions.communityId=vc.getCurrentCommunity().communityId
                var param = {
                    params:vc.component.listFloorInfo.conditions
                };

               //发送get请求
               vc.http.get('listFloor',
                            'list',
                             param,
                             function(json,res){
                                var listFloorData =JSON.parse(json);

                                vc.component.listFloorInfo.total = listFloorData.total;
                                vc.component.listFloorInfo.records = listFloorData.records;
                                vc.component.listFloorInfo.floors = listFloorData.apiFloorDataVoList;

                                vc.emit('pagination','init',{
                                    total:vc.component.listFloorInfo.records,
                                    currentPage:_page
                                });
                             },function(errInfo,error){
                                console.log('请求失败处理');
                             }
                           );

            },
            _openAddFloorModal:function(){ //打开添加框
                vc.emit('addFloor','openAddFloorModal',{});
            },
            _openDelFloorModel:function(_floor){ // 打开删除对话框
                vc.emit('deleteFloor','openFloorModel',_floor);
            },
            _openEditFloorModel:function(_floor){
                vc.emit('editFloor','openEditFloorModal',_floor);
            },
            _queryFloorMethod:function(){
                vc.component._listFloorData(DEFAULT_PAGE, DEFAULT_ROWS);
            }
        }
    })
})(window.vc);