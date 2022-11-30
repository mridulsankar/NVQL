require 'rubygems'
#require 'open-uri'
#require 'activerecord'
require 'pg'
require 'ruby-graphviz'



begin
	
	agConn = PG.connect(:user => "postgres", :password => "abcdefgh", :dbname => "TVA", :host => 'localhost', :port => '5432')


	Host_Name=[]
	res=agConn.exec("select host_id, host_name from host") 
	res.each do |row|
		id=row['host_id'].to_i
		Host_Name[id]=row['host_name']
		puts Host_Name[id]
	end

	
	g=GraphViz::new("G" )
	
	if ARGV[0]==nil then
		puts "Please provide output file name..."
		exit
	end

	
	res=agConn.exec("select distinct * from Qc")

	condNodes=[]
	cNodes=[]
	i=0

	
	res.each do |row|
		label="("+Host_Name[row['host_id'].to_i]+", "+row['cond'].to_s+")"
		
		condNodes[i]=label
		cNodes[i]=g.add_nodes("#{label}", :shape => "none")
		puts "condNodes[#{i}]=#{label} " 
		i+=1
	end

	condNodeCount=i

	res=agConn.exec("select distinct * from Qe")
	exploitNodes=[]
	eNodes=[]
	i=0
	res.each do |row|
		label=row['vulnerability'].to_s+"("+Host_Name[row['src_id'].to_i]+", "+Host_Name[row['dst_id'].to_i]+")"
		exploitNodes[i]=label
		eNodes[i]=g.add_nodes("#{label}", :shape => "ellipse" )
		puts "exploitNodes[#{i}]=#{label}"
		i+=1
	end

	expNodeCount=i

	
	res=agConn.exec("select distinct * from Qce")
	i=0
	res.each do |row|
		cLabel="("+Host_Name[row['host_id'].to_i]+", "+row['cond'].to_s+")"
		cIndex=condNodes.index(cLabel)
		eLabel=row['vulnerability'].to_s+"("+Host_Name[row['src_id'].to_i]+", "+Host_Name[row['dst_id'].to_i]+")"
		eIndex=exploitNodes.index(eLabel)
		
		g.add_edges(cNodes[cIndex],eNodes[eIndex])
		i+=1
		
	end

	preCondEdgeCount=i

	res=agConn.exec("select distinct * from Qec")
	i=0
	res.each do |row|

		cLabel="("+Host_Name[row['host_id'].to_i]+", "+row['cond'].to_s+")"
		cIndex=condNodes.index(cLabel)
		eLabel=row['vulnerability'].to_s+"("+Host_Name[row['src_id'].to_i]+", "+Host_Name[row['dst_id'].to_i]+")"
		eIndex=exploitNodes.index(eLabel)

		g.add_edges(eNodes[eIndex], cNodes[cIndex])
		i+=1
		
	end

	postCondEdgeCount=i

	
	totalNodeCount=condNodeCount+expNodeCount
	totalEdgeCount=preCondEdgeCount+postCondEdgeCount

	puts "No. of Condition Nodes = #{condNodeCount}"
	puts "No. of Exploit Nodes = #{expNodeCount}"

	puts "No. of Pre-Condition Edges = #{preCondEdgeCount}"
	puts "No. of Post-Condition Edges = #{postCondEdgeCount}"


	puts "Total No. of Nodes = #{totalNodeCount}"
	puts "Total No. of Edges = #{totalEdgeCount}"

	#imgFileName=ARGV[0]+".gif"
	#g.output(:gif => "#{imgFileName}")	
	imgFileName=ARGV[0]+".jpg"
	g.output(:jpg => "#{imgFileName}")	
	agConn.close() if agConn

=begin
	hid=1
	res=agConn.exec("select host_name from host where host_id=#{hid}") 
	res.each do |row|
		puts row['host_name']
	end
=end
	
end
