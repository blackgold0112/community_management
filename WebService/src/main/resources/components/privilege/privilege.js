(function(vc){

    vc.extends({
        data:{
            privilegeInfo:{
                _currentPgId:"",
                _currentPgName:"",
                _currentStoreId:"9999",
                _privileges:[]
            }
        },
        _initMethod:function(){

        },
        _initEvent:function(){
            vc.component.$on('privilege_group_event',function(_pgObj){
                vc.component.privilegeInfo._currentPgId = _pgObj._pgId;
                vc.component.privilegeInfo._currentPgName = _pgObj._pgName;
                vc.component.privilegeInfo._currentStoreId = _pgObj._storeId;
                //调用接口查询权限
                vc.component._loadPrivilege(_pgObj._pgId);
            });
        },
        methods:{
            _loadPrivilege:function(_pgId){
                vc.component.privilegeInfo._privileges=[];
                var param = {
                                    params:{pgId:_pgId}
                                };

                                //发送get请求
               vc.http.get('privilege',
                            'listPrivilege',
                             param,
                             function(json){
                                var _privileges = JSON.parse(json);
                                vc.component.privilegeInfo._privileges = _privileges;
                             },function(){
                                console.log('请求失败处理');
                             }
                           );
            },
            openAddPrivilegeModel:function(){
                vc.component.$emit('addPrivilege_openPrivilegeModel',{
                            pgId:vc.component.privilegeInfo._currentPgId
                });
            }
        }
    });

})(window.vc);