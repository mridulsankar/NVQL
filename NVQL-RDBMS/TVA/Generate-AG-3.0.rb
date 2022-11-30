#require 'rubygems'
#require 'open-uri'
#require 'postgres'
require 'pg'
#require 'dbi'



def iterateOnce(agConn)
	#Compute Q1
	#agConn.exec("delete from Q1")
	
	#Consider Firewall Filters
	agConn.exec("drop table if exists T1")
	agConn.exec("create table T1 (Src_Id INTEGER, Dst_Id INTEGER, Vulnerability VARCHAR(50), Host_Id INTEGER , Cond VARCHAR(50))")
	agConn.exec("insert into T1 (Src_Id, Dst_Id, Vulnerability, Host_Id, Cond) select * from Q1")
	agConn.exec("delete from Q1")
	#agConn.exec("insert into Q1 (Src_Id, Dst_Id, Vulnerability, Host_Id, Cond) select distinct Src_Id, Dst_Id, Vulnerability, Host_Id, Qc.Cond from HH, VC, Qc where HH.Src_Id=Qc.Host_Id or HH.Dst_Id=Qc.Host_Id except select * from T1")
	agConn.exec("insert into Q1 (Src_Id, Dst_Id, Vulnerability, Host_Id, Cond) select distinct Src_Id, Dst_Id, Vulnerability, Host_Id, Qc.Cond from HH, VC, Qc where HH.Src_Id=Qc.Host_Id or HH.Dst_Id=Qc.Host_Id")

	
	#agConn.exec("create table T11 (Src_Id INTEGER, Dst_Id INTEGER, Vulnerability VARCHAR(50), Host_Id INTEGER , Cond VARCHAR(50))")
	#agConn.exec("insert into T11 (Src_Id, Dst_Id, Vulnerability, Host_Id, Cond) select distinct Src_Id, Dst_Id, Vulnerability, Dst_Id, FF.Cond from FF, VC")
	#agConn.exec("insert into T1 (Src_Id, Dst_Id, Vulnerability, Host_Id, Cond) select Q1.Src_Id, Q1.Dst_Id, Q1.Vulnerability, Q1.Host_Id, Q1.Cond from Q1 left join T11 on Q1.Src_Id=T11.Src_Id and Q1.Dst_Id=T11.Dst_Id and Q1.Vulnerability=T11.Vulnerability and Q1.Host_Id=T11.Host_Id and Q1.Cond=T11.Cond where T11.Src_Id is NULL and T11.Dst_Id is NULL and T11.Vulnerability is NULL and T11.Host_Id is NULL and T11.Cond is NULL")
	#
	#agConn.exec("insert into Q1 (Src_Id, Dst_Id, Vulnerability, Host_Id, Cond) select * from T1") 
	#agConn.exec("drop table T1")
	#agConn.exec("drop table T11")

	#Compute Q2
	agConn.exec("delete from Q2") 
	#agConn.exec("create table T2 (Src_Id INTEGER, Dst_Id INTEGER, Vulnerability VARCHAR(50), Host_Id INTEGER , Cond VARCHAR(50))")
	#agConn.exec("insert into T2 (Src_Id, Dst_Id, Vulnerability, Host_Id, Cond) select distinct Src_Id, Dst_Id, Vulnerability, Dst_Id, CV.Cond from HH, CV where Place='D' UNION select distinct Src_Id, Dst_Id, Vulnerability, Src_Id, CV.Cond from HH, CV where Place='S'")
	#agConn.exec("insert into Q2 (Src_Id, Dst_Id, Vulnerability, Host_Id, Cond) select T2.Src_Id, T2. Dst_Id, T2.Vulnerability, T2.Host_Id, T2.Cond from T2 left join Q1 on T2.Src_Id=Q1.Src_Id and T2.Dst_Id=Q1.Dst_Id and T2.Vulnerability=Q1.Vulnerability and T2.Host_Id=Q1.Host_Id and T2.Cond=Q1.Cond where Q1.Src_Id is NULL and Q1.Dst_Id is NULL and Q1.Vulnerability is NULL and Q1.Host_Id is NULL and Q1.Cond is NULL")
	#agConn.exec("drop table T2")
	agConn.exec("insert into Q2 (Src_Id, Dst_Id, Vulnerability, Host_Id, Cond) select distinct Src_Id, Dst_Id, Vulnerability, Dst_Id, Cond from HH, CV where Place='D' union select distinct Src_Id, Dst_Id, Vulnerability, Src_Id, Cond from HH, CV where Place='S' except select * from Q1")
	#Compute Qe	

	agConn.exec("drop table if exists Te1")
	agConn.exec("drop table if exists Te2")
	agConn.exec("drop table if exists Temp1")

	agConn.exec("create table Te1 (Src_Id INTEGER, Dst_Id INTEGER, Vulnerability VARCHAR(50))")
	agConn.exec("create table Te2(Src_Id INTEGER, Dst_Id INTEGER, Vulnerability VARCHAR(50))")
	agConn.exec("create table Temp1 (Src_Id INTEGER, Dst_Id INTEGER, Vulnerability VARCHAR(50))")	

	agConn.exec("insert into Te1 (Src_Id, Dst_Id, Vulnerability) select distinct Src_Id, Dst_Id, Vulnerability from HH, CV")
	agConn.exec("insert into Te2 (Src_Id, Dst_Id, Vulnerability) select distinct Src_Id, Dst_Id, Vulnerability from Q2")
	agConn.exec("insert into Temp1 (Src_Id, Dst_Id, Vulnerability) select Te1.Src_Id, Te1.Dst_Id, Te1.Vulnerability from Te1 left join Te2 on Te1.Src_Id=Te2.Src_Id and Te1.Dst_Id=Te2.Dst_Id and Te1.Vulnerability=Te2.Vulnerability  where Te2.Src_Id is NULL and Te2.Dst_Id is NULL and Te2.Vulnerability is NULL UNION select * from Qe")
	agConn.exec("insert into Qe (Src_Id, Dst_Id, Vulnerability) select * from Temp1") 
	

	#agConn.exec("delete from Qe")
	#agConn.exec("insert into Qe (Src_Id, Dst_Id, Vulnerability) ((select distinct Src_Id, Dst_Id, Vulnerability from HH, CV except select distinct Src_Id, Dst_Id, Vulnerability from Q2)UNION select * from Qe)")
	

	#Compute Qce
	#agConn.exec("create table Temp2 (Host_Id INTEGER, Cond VARCHAR(50), Src_Id INTEGER, Dst_Id INTEGER, Vulnerability VARCHAR(50))")
	#agConn.exec("insert into Temp2 (Host_Id, Cond, Src_Id, Dst_Id, Vulnerability) select distinct Dst_Id, CV.Cond, Src_Id, Dst_Id, Qe.Vulnerability from Qe, CV where Place='D' and Qe.Vulnerability=CV.Vulnerability UNION select distinct Src_Id, CV.Cond, Src_Id, Dst_Id, Qe.Vulnerability from Qe, CV where Place='S' and Qe.Vulnerability=CV.Vulnerability UNION select * from Qce")
	#agConn.exec("delete * from Qce")
	agConn.exec("insert into Qce (Host_Id, Cond, Src_Id, Dst_Id, Vulnerability) select distinct Dst_Id, CV.Cond, Src_Id, Dst_Id, Qe.Vulnerability from Qe, CV where Place='D' and Qe.Vulnerability=CV.Vulnerability UNION select distinct Src_Id, CV.Cond, Src_Id, Dst_Id, Qe.Vulnerability from Qe, CV where Place='S' and Qe.Vulnerability=CV.Vulnerability")
	#agConn.exec("insert into Qce (Host_Id, Cond, Src_Id, Dst_Id, Vulnerability) select  * from Temp2")
	#agConn.exec("drop table Temp2")
	
	#Compute Qec
	#agConn.exec("create table Temp3 (Src_Id INTEGER, Dst_Id INTEGER, Vulnerability VARCHAR(50), Host_Id INTEGER, Cond VARCHAR(50))")
	agConn.exec("insert into Qec (Src_Id, Dst_Id, Vulnerability, Host_Id, Cond) select Src_Id, Dst_Id, Qe.Vulnerability, Dst_Id, VC.Cond from Qe, VC where Qe.Vulnerability=VC.Vulnerability")
	#agConn.exec("delete from Qec")
	#agConn.exec("insert into Qec (Src_Id, Dst_Id, Vulnerability, Host_Id, Cond) select  * from Temp3")
	#agConn.exec("drop table Temp3")
	
	#Update Qc
	#agConn.exec("create table Temp4 (Host_Id INTEGER , Cond VARCHAR(50))")
	#agConn.exec("insert into Temp4 (Host_Id, Cond) select Host_Id, Cond from Qec UNION select * from Qc")
	#agConn.exec("delete from Qc")
	agConn.exec("insert into Qc (Host_Id, Cond) select Host_Id, Cond from Qec")
	#agConn.exec("drop table Temp4")
end


def createTempTables(conn)
	conn.exec("create table Q1 (Src_Id INTEGER, Dst_Id INTEGER, Vulnerability VARCHAR(50), Host_Id INTEGER , Cond VARCHAR(50))")
	conn.exec("create table Q2 (Src_Id INTEGER, Dst_Id INTEGER, Vulnerability VARCHAR(50), Host_Id INTEGER , Cond VARCHAR(50))")
	conn.exec("create table Qc (Host_Id INTEGER , Cond VARCHAR(50))")
	conn.exec("create table Qe (Src_Id INTEGER, Dst_Id INTEGER, Vulnerability VARCHAR(50))")
	conn.exec("create table Qce (Host_Id INTEGER, Cond VARCHAR(50), Src_Id INTEGER, Dst_Id INTEGER, Vulnerability VARCHAR(50))")
	conn.exec("create table Qec (Src_Id INTEGER, Dst_Id INTEGER, Vulnerability VARCHAR(50), Host_Id INTEGER, Cond VARCHAR(50))")
end

def dropTempTables(conn)
	conn.exec("drop table if exists Q1")
	conn.exec("drop table if exists Q2")
	conn.exec("drop table if exists Qc")
	conn.exec("drop table if exists Qe")
	conn.exec("drop table if exists Qce")
	conn.exec("drop table if exists Qec")
end


begin
	
	agConn = PG.connect(:user => "postgres", :password => "abcdefgh", :dbname => "TVA", :host => 'localhost', :port => '5432')
	
	dropTempTables(agConn)
	createTempTables(agConn)
	
	agConn.exec("insert into Qc (Host_Id, Cond) Select * from HC")

	
	iteration=0
	begin
		iteration+=1
		#puts "MSB"
		res=agConn.exec("select distinct * from Qc;")
		noRows=0
		res.each do 
			noRows=noRows+1
		end
	
		condCountOld=noRows
	
		
		iterateOnce(agConn)
	
		res=agConn.exec("select distinct * from Qc;")
		noRows=0
		res.each do 
			noRows=noRows+1
		end
		condCountNew=noRows
	
		diff=condCountNew-condCountOld
		puts "Iteration #{iteration}: # of Conditions = #{condCountOld}, # of New Conditions Generated = #{diff}"
	end while condCountNew>condCountOld

	agConn.close() if agConn
end
