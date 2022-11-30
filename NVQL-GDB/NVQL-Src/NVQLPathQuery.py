from neo4j import GraphDatabase
from pandas import DataFrame
from datetime import datetime
import time

from sympy import true

class GDBAGGen:
    def __init__(self, uri, user, password):
        try:
            self.driver = GraphDatabase.driver(uri, auth=(user, password))
        except Exception as e:
            print("Failed to initialize Neo4j driver:", e)

    def close(self):
        if self.driver is not None:
            self.driver.close()
    
    def query(self, query, db=None):
        assert self.driver is not None, "Driver is not initialized!"
        session=None
        response=None
        try:
            if db is not None:
                session=self.driver.session(database=db)
            else:
                session=self.driver.session()
            
            response=list(session.run(query))
        except Exception as e:
            print("Query failed:", e)
        finally:
            if session is not None:
                session.close()
        return response

Neo4jDBCon = GDBAGGen("bolt://localhost:7687", "neo4j", "abcdefgh")


HARD_CODED = true

if HARD_CODED:
    srcIP='192.168.148.1'
    dstIP='192.168.148.187'
else:
    print("Enter source IP address: ", end='')
    srcIP = input()
    print("Enter destination IP address: ", end='')
    dstIP = input()


query = "match (src:aghost{ipAddr: '" + srcIP + "'})<-[:AT_HOST]-(gi1:aggi)-[:INSTANCE_OF]->(g:agag{name:'user'}), " + \
        "(dst:aghost{ipAddr: '" + dstIP + "'})<-[:AT_HOST]-(gi2:aggi)-[:INSTANCE_OF]->(g:agag{name:'user'}) " + \
        "match p=shortestPath((gi1)-[:NEXT*1..20]->(gi2)) return nodes(p), length(p)"


print(query)
t1=time.time()
result=Neo4jDBCon.query(query)
t2=time.time()
print("Path query execution time = " + str(t2-t1) + " seconds")
dtf_data = DataFrame([dict(_) for _ in result])
print(dtf_data)

Neo4jDBCon.close()


