ssh -f $DFS_USERNAME@csel-kh1250-11.cselabs.umn.edu "cd $PROJ_PATH; ant server"
echo "sleep 2 seconds"
sleep 2
ssh -f $DFS_USERNAME@csel-kh1250-12.cselabs.umn.edu "cd $PROJ_PATH; ant server"
echo "sleep 2 seconds"
sleep 2
ssh -f $DFS_USERNAME@csel-kh1250-13.cselabs.umn.edu "cd $PROJ_PATH; ant server"
echo "sleep 2 seconds"
sleep 2
ssh -f $DFS_USERNAME@csel-kh1250-14.cselabs.umn.edu "cd $PROJ_PATH; ant server"
echo "sleep 2 seconds"
sleep 2
ssh -f $DFS_USERNAME@csel-kh1250-15.cselabs.umn.edu "cd $PROJ_PATH; ant server"
echo "sleep 2 seconds"
sleep 2
ssh -f $DFS_USERNAME@csel-kh1250-16.cselabs.umn.edu "cd $PROJ_PATH; ant server"
echo "sleep 2 seconds"
sleep 2
ssh -f $DFS_USERNAME@csel-kh1250-17.cselabs.umn.edu "cd $PROJ_PATH; ant server"
echo "sleep 2 seconds and then start clients"