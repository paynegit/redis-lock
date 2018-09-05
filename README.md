Here are steps to set up redis cluster
Note: ${code_dir} is current working dir
1, Pull and create one redis docker container
docker pull redis
chmod -R 777 ${code_dir}/redis-conf
docker run -d -p 6379:6379 -p 6380:6380 -p 6381:6381 -p 6382:6382 -p 6383:6383 -p 6384:6384 -p 6385:6385 -v ${code_dir}/redis-conf:/data --name redis redis
#docker start redis
docker exec -it redis bash


2, Start 6 redis servers, three are masters, other three are slaves
redis-server redis-conf/redis-6380.conf
redis-server redis-conf/redis-6381.conf
redis-server redis-conf/redis-6382.conf
redis-server redis-conf/redis-6383.conf
redis-server redis-conf/redis-6384.conf
redis-server redis-conf/redis-6385.conf

3, Commands to enter into every redis server
redis-cli -h 127.0.0.1 -p 6380
redis-cli -h 127.0.0.1 -p 6381
redis-cli -h 127.0.0.1 -p 6382
redis-cli -h 127.0.0.1 -p 6383
redis-cli -h 127.0.0.1 -p 6384
redis-cli -h 127.0.0.1 -p 6385


4, Make every redis server know each other
redis-cli -h 127.0.0.1 -p 6380
CLUSTER MEET 127.0.0.1 6381
CLUSTER MEET 127.0.0.1 6382
CLUSTER MEET 127.0.0.1 6383
CLUSTER MEET 127.0.0.1 6384
CLUSTER MEET 127.0.0.1 6385
Check the redis cluster nodes
CLUSTER NODES
exit

5, In the redis docker container, assign every master server with proper slots:
redis-cli -h 127.0.0.1 -p 6380 cluster addslots {0..5461}
redis-cli -h 127.0.0.1 -p 6381 cluster addslots {5462..10922}
redis-cli -h 127.0.0.1 -p 6382 cluster addslots {10923..16383}


6, Get the node id of redis master on which assigned slots from above command and salve servers will replicate them:
redis-cli -h 127.0.0.1 -p 6380
CLUSTER NODES
exit

redis-cli -h 127.0.0.1 -p 6383 cluster replicate a18c746c5fb0990fb8ef0b1537b4c1cc7e85df67
redis-cli -h 127.0.0.1 -p 6384 cluster replicate 20fd02a2af014a786c2ee5f086dd20cd111d3ff2
redis-cli -h 127.0.0.1 -p 6385 cluster replicate b63fd9259599a9ecc05d0187aa13ec2edbe8cb58

7, Run following command to start application
 java -jar ${code_dir}/target/redis-lock-0.0.1-SNAPSHOT.jar
8, Access this url and check the backend log information
 http://localhost:8080/testlock