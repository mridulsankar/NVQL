from neo4j import GraphDatabase


class AGQL2GDBAGGen:
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



def propsDict2Str(properties):
    propStr=""
    for key in properties:
        propStr+=key+": "+"'"+str(properties[key])+"', "
        
    return propStr[:-2]


def createHosts(Neo4jDBCon):
    #Get properties of all host entities

    query = "match (n:host) where not exists(n.Entity_Type_Unique_Attr_List) return properties(n) as props"
    
    result=Neo4jDBCon.query(query)
    #result consists of number of records
    
    for record in result:
        properties=record["props"]
        #properties is a python dictionary
        propStr="{type: 'H', "+ propsDict2Str(properties)+"}"
        query="create (:aghost " + propStr + ")"
        print(query)
        
        Neo4jDBCon.query(query)
    print("All hosts created")



def createServiceInstances(Neo4jDBCon):

    #Get properties of all service entities, name of corresponding host where it runs and CVE Id of vulenrabilities it has

    query = """match (h:host)<-[:`RELATION:runAt`]-(n:service)-[:`RELATION:hasVuln`]->(v:vulnerability) where not exists(n.Entity_Type_Unique_Attr_List) 
                return h.name as hname, properties(n) as props, v.cveId as vcveId"""
    
    result=Neo4jDBCon.query(query)
    
    #result consists of number of records
    
    snames=set()
    for record in result:
        properties=record["props"]
        vcveId=record["vcveId"]
        hname=record["hname"]
        #properties is a python dictionary
        sPropStr="{type: 'S', "
        siPropStr="{type: 'SI', "

        
        sname=""
        for key in properties:
            #Key "name" and its value goes to service instance 
            if key.__eq__("name"):
                siPropStr+=key+": "+"'"+str(properties[key])+"', "
            #Key "swName" and its value goes to service as key "name" 
            elif key.__eq__("swName"):
                sname=str(properties[key])
                sPropStr+="name"+": "+"'"+sname+"', "
            #Rest of the key values go to service
            else:
                sPropStr+=key+": "+"'"+str(properties[key])+"', "
        
        #Remove the last comma and blank space and put a closing brace at the end
        sPropStr=sPropStr[:-2]+"}"
        siPropStr=siPropStr[:-2]+"}"

        serviceExists=False
        print(sname, snames)
        if sname in snames:
            serviceExists=True
        

        if serviceExists:
            print("Service Exists")
            #query="match (v:agvulnerability) where v.cveId='" + vcveId + "' match (h:aghost) where h.name='" + hname + \
            #        "' match (s:agservice) where s.name='" + sname + "' "\
            #        "with v, h, s create (si:agsi " + siPropStr + ")" + "-[:INSTANCE_OF]-> (s), " + \
            #        "(s)-[:HAS_VULN]->(v), (si)-[:RUN_AT]->(h);"
            query="match (h:aghost) where h.name='" + hname + \
                    "' match (s:agservice) where s.name='" + sname + "' "\
                    "with h, s create (si:agsi " + siPropStr + ")" + "-[:INSTANCE_OF]-> (s), " + \
                    "(si)-[:AT_HOST]->(h);"
        else:    
            print("Service Does Not Exist")
            query="match (v:agvulnerability) where v.cveId='" + vcveId + "' match (h:aghost) where h.name='" + hname + "' " + \
                    "with v, h create (si:agsi " + siPropStr + ")" + "-[:INSTANCE_OF]-> " + "(s:agservice " + sPropStr + "), " + \
                    "(s)-[:HAS_VULN]->(v), (si)-[:AT_HOST]->(h);"
        
        print(query)
        
        Neo4jDBCon.query(query)
        snames.add(sname)

    print("All services and service instances created")


def createVulnerabilities(Neo4jDBCon):
    #Get properties of all vulnerability entities

    query = "match (n:vulnerability) where not exists(n.Entity_Type_Unique_Attr_List) return properties(n) as props"
    
    result=Neo4jDBCon.query(query)

    #result consists of number of records
    
    for record in result:
        properties=record["props"]
        #properties is a python dictionary
        propStr="{type: 'V', "+ propsDict2Str(properties)+"}"
        query="create (:agvulnerability " + propStr + ")"
        print(query)
        
        Neo4jDBCon.query(query)
    print("All vulnerabilities created")

def createPrivileges(Neo4jDBCon):
     #Get properties of all privilege entities

    query = "match (n:privilege)-[:`RELATION:atHost`]->(h:host) where not exists(n.Entity_Relation_List)" + \
             "return properties(n) as props, h.name as hname;"

    
    result=Neo4jDBCon.query(query)
    
    #result consists of number of records
    
    for record in result:
        properties=record["props"]
        hname=record["hname"]
        agPropStr="{type: 'AG', "
        giPropStr="{type: 'GI', "

        for key in properties:
            #Key "name" and its value goes to goal instance node
            if key.__eq__("name"):
                giPropStr+=key+": "+"'"+str(properties[key])+"', "
            #Key "privType" and its value goes to attacker goal node as key "name" 
            elif key.__eq__("privType"):
                privname=str(properties[key])
                agPropStr+="name"+": "+"'"+privname+"', "
            #Rest of the key value pairs go to goal instance node
            else:
                giPropStr+=key+": "+"'"+str(properties[key])+"', "
        
        #Remove the last comma and blank space and put a closing brace at the end
        giPropStr=giPropStr[:-2]+"}"
        agPropStr=agPropStr[:-2]+"}"

        query="match (h:aghost) where h.name='" + hname + "' " + \
                    "with h create (gi:aggi " + giPropStr + ")" + "-[:INSTANCE_OF]-> " + "(ag:agag " + agPropStr + "), " + \
                    "(gi)-[:AT_HOST]->(h), (a1:attacker{type: 'A',name: 'attacker'}), (a1)-[:START_GOAL]->(gi);"
        
        print(query)
        
        Neo4jDBCon.query(query)
    print("Attacker goal and goal instances created")


def createServiceAccessInstances(Neo4jDBCon):
    #Get properties of all vulnerability entities

    
    query = "match (s:service)<-[:`RELATION:accessTo`]-(r:reachability)-[:`RELATION:accessBy`]->(h:host) where not exists(r.Entity_Relation_List) return properties(r) as props, h.name as hname, s.name as sname;"
    
    
    result=Neo4jDBCon.query(query)
    #result consists of number of records
    
    for record in result:
        properties=record["props"]
        hname=record["hname"]
        sname=record["sname"]

        #properties is a python dictionary
        propStr="{type: 'SAI', "+ propsDict2Str(properties)+"}"
        query="match (h:aghost{name:'"+hname+"'}) match (s:agsi{name:'"+sname+"'}) with h, s create (s)<-[:ACCESS_TO]-(:agSAI " + propStr + ")-[:ACCESS_BY]->(h)"
        print(query)
        
        Neo4jDBCon.query(query)
    print("All service access instances created")



Neo4jDBCon = AGQL2GDBAGGen("bolt://localhost:7687", "neo4j", "abcdefgh")

createHosts(Neo4jDBCon)
createVulnerabilities(Neo4jDBCon)
createServiceInstances(Neo4jDBCon)
createServiceAccessInstances(Neo4jDBCon)
createPrivileges(Neo4jDBCon)


Neo4jDBCon.close()