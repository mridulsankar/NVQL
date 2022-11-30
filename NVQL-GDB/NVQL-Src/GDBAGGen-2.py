from neo4j import GraphDatabase
from pandas import DataFrame
import time


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


t1=time.time()

Neo4jDBCon = GDBAGGen("bolt://localhost:7687", "neo4j", "abcdefgh")

count=0
while True:
    count=count+1
    #print("count = ", count)
    query = "match (src)<-[:AT_HOST]-(gi)-[:INSTANCE_OF]->(g), (src)<-[:ACCESS_BY]-(sai) where g.name='user' " + \
        "with src, sai, gi, g match (sai)-[:ACCESS_TO]->(si)-[:INSTANCE_OF]->(s)-[:HAS_VULN]->(v), (si)-[:AT_HOST]->(dst) " + \
            "where not (sai)-[]->(v) return sai.name, s.name, v.name, src.name, dst.name, gi.name, g.name"
    
    #    print(query)
    #    print()

    
    result=Neo4jDBCon.query(query)
    dtf_data = DataFrame([dict(_) for _ in result])
    nrows=len(dtf_data.index)
    #print("Iteration: "+str(count)+" -> "+"no. of exploits = "+ str(nrows))
    if(nrows==0):
        break
    
    #    print(dtf_data)
    #    print()
    query = "match (gi:aggi)-[:INSTANCE_OF]->(g:agag) where g.name='user' return count(gi) as count"
    result_2=Neo4jDBCon.query(query)
    dtf_data_2 = DataFrame([dict(_) for _ in result_2])
    oldcount = dtf_data_2.iloc[0]['count']

    for rowindex in range(0, nrows):
        saiName=dtf_data.iloc[rowindex]['sai.name']
        ogiName=dtf_data.iloc[rowindex]['gi.name']
        gName=dtf_data.iloc[rowindex]['g.name']
        dstName=dtf_data.iloc[rowindex]['dst.name']
        srcName=dtf_data.iloc[rowindex]['src.name']
        vName=dtf_data.iloc[rowindex]['v.name']
        aiName = vName+"("+srcName+","+dstName+")"
        ngName="user"
        ngiName = ngName+dstName[1:]
        #ngiName = ngName+"("+dstName+")"

        query = "match (sai:agSAI), (ogi:aggi), (g:agag), (dst:aghost), (v:agvulnerability) where sai.name='"+ saiName + "' and ogi.name='" + ogiName + \
           "' and g.name='" + gName + "' and dst.name='" + dstName + "' and v.name='" + vName + "' " + \
                "merge (ai:agai{type: \"AI\", name: '" + aiName + "'}) " + \
                "merge (ngi:aggi{type: \"GI\", name: '" + ngiName + "'}) " + \
                "on create set ngi.iscreated='true' " + \
                "on match set ngi.iscreated='false' " + \
                "merge (sai)-[:REQUIRE]->(ai) " + \
                "merge (ogi)-[:REQUIRE]->(ai) " + \
                "merge (ai)-[:IMPLY]->(ngi) " + \
                "merge (ogi)-[:NEXT]->(ngi) " + \
                "merge (ngi)-[:INSTANCE_OF]->(g) " + \
                "merge (ngi)-[:AT_HOST]->(dst) " + \
                "merge (sai)-[:EXPLOIT]->(v) " + \
                "return ai.name, ngi.name"

       
            #    print(query) 
            #    print()

        result=Neo4jDBCon.query(query)
        
            #dtf_data2 = DataFrame([dict(_) for _ in result])
            #print(dtf_data2)

    query = "match (gi:aggi)-[:INSTANCE_OF]->(g:agag) where g.name='user' return count(gi) as count"
    result_2=Neo4jDBCon.query(query)
    dtf_data_2 = DataFrame([dict(_) for _ in result_2])
    newcount=dtf_data_2.iloc[0]['count']

    #print(dtf_data)
    print("Iteration: "+str(count)+" -> "+"no. of new conditions = "+ str(newcount-oldcount))
    print()


        #print(query) 
        #print()

    #result=Neo4jDBCon.query(query)
    #dtf_data = DataFrame([dict(_) for _ in result])
    #print("Iteration: "+str(count)+" -> "+"no. of new conditions = "+ str(dtf_data.iloc[0]['count']))
    #print()
    #print(dtf_data)
    #print()    

Neo4jDBCon.close()

t2=time.time()

print("NVQL AG Generation Time = " + str(t2-t1) + " seconds")
