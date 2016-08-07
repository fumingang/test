package test;

import java.util.HashMap;
import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.text.*;

public class utilities {
	
	public static void main(String[] args) {
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		
		System.out.println("test1");
		System.out.println("test0");
		
/*		
		Double x = 3.0;
		Double y = 1.0;
		Double x1 = 0.0;
		Double y1 = 0.0;
		Double x2 = 1.0;
		Double y2 = 0.0;
		System.out.println(plDist(x,y,x1,y1,x2,y2));
		System.out.println(distance(x1,y1,x2,y2));
		System.out.println(Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1)));
*/		
		
		LinkedList<coordinate> orders = new LinkedList<coordinate>();
		HashMap<Integer, LinkedList<coordinate>> node_lookup = new HashMap<Integer, LinkedList<coordinate>>();
		LinkedList<coordinate> nodes = new LinkedList<coordinate>();
		HashMap<Integer, Integer> id_to_index = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> index_to_id = new HashMap<Integer, Integer>();
		HashMap<Integer, HashMap<Integer, distance>> links = new HashMap<Integer, HashMap<Integer, distance>>();
		HashMap<Integer, HashMap<Integer, Double>> distance_matrix = new HashMap<Integer, HashMap<Integer, Double>>();
		HashMap<Integer, HashMap<Integer, Integer>> next_node_matrix = new HashMap<Integer, HashMap<Integer, Integer>>();
		HashMap<Integer, HashMap<Integer, LinkedList<Integer>>> path_matrix = new HashMap<Integer, HashMap<Integer, LinkedList<Integer>>>();
		LinkedList<segment> segments = new LinkedList<segment>();
		HashMap<Integer, LinkedList<segment>> segments_lookup = new HashMap<Integer, LinkedList<segment>>();

        // read map nodes file, generate a nodes linkedlist and a zip to nodes lookup hashmap
        try{
            BufferedReader br = new BufferedReader(new FileReader("input/node_list.csv"));
            String strLine = br.readLine(); //remove the header line before reading
            int index = 0;
            	                    
            while ((strLine = br.readLine()) != null)   { // read file line by line
                    String[] data = strLine.split(",");
                    
                    // convert a string of service time into double number
                    Integer id = Integer.parseInt(data[0]);
                    Double lat = Double.parseDouble(data[1]);
                    Double lng = Double.parseDouble(data[2]);
                    Integer l_zip = Integer.parseInt(data[3]);
                    Integer r_zip = Integer.parseInt(data[4]);
                    
                    coordinate tmp = new coordinate(lat, lng);
                    tmp.setId(id);
                    
                    nodes.add(tmp);
                    
                    id_to_index.put(id, index);
                    index_to_id.put(index, id);
                    
                    if(!node_lookup.containsKey(l_zip)) {
                    	node_lookup.put(l_zip, new LinkedList<coordinate>());
                    }
                    tmp.setZip(l_zip);
                    node_lookup.get(l_zip).add(tmp);
                	if(r_zip != l_zip) {
                    	if(!node_lookup.containsKey(r_zip)) {
                    		node_lookup.put(r_zip, new LinkedList<coordinate>());                		
                    	}
                    	coordinate tmp2 = new coordinate(lat, lng);
                    	tmp2.setId(id);
                    	tmp2.setZip(r_zip);
                    	node_lookup.get(r_zip).add(tmp2);
                	}
                	
                	index++;
            }
            br.close(); //Close the input stream
	    } catch (Exception e){//Catch exception if any
	            System.err.println("Error: " + e.getMessage());
	    }
        
        //for(int i = 0; i < index_to_id.size(); i++) {
        //	System.out.println(i + " " + index_to_id.get(i));
        //}
        
        System.out.println("finished reading nodes...");
        
        // read the road links file
        try{
            BufferedReader br = new BufferedReader(new FileReader("input/link_list.csv"));
            String strLine = br.readLine();
            	                    
            while ((strLine = br.readLine()) != null)   { // read file line by line
                    String[] data = strLine.split(",");
                    int origin = Integer.parseInt(data[0]);
                    if(!links.containsKey(origin)) {
                    	links.put(origin, new HashMap<Integer, distance>());
                    }       
                    
                    int dest = Integer.parseInt(data[1]);                    
                    double dist = Double.parseDouble(data[2]);
                    double time = Double.parseDouble(data[3]);
                    
                    distance tmp = new distance(dist, time);
                    
                    links.get(origin).put(dest, tmp);
            }           
            br.close();
	    } catch (Exception e){//Catch exception if any
	            System.err.println("Error: " + e.getMessage());
	    }
        
        System.out.println("finished reading road links");
        
        // define and initialize next node matrix 2
        int[][] next_node_matrix2 = new int[nodes.size()][nodes.size()];
        
        // read next node matrix file
        try{
            BufferedReader br = new BufferedReader(new FileReader("input/next_node_matrix.csv"));            
            String strLine;
            int i = 0;                       
            	                    
            while ((strLine = br.readLine()) != null)   { // read file line by line
                    String[] data = strLine.split(",");
                    
                    for(int j = 0; j < data.length; j++) {  
                    	if(data[j].equals("NA")) {
                    		next_node_matrix2[i][j] = -1; //-1 means there is no 
                    	} else {
                    		next_node_matrix2[i][j] = Integer.parseInt(data[j]);  
                    	}
                    }
                    i++;
            }
            br.close();
	    } catch (Exception e){//Catch exception if any
	            System.err.println("Error: " + e.getMessage());
	    }
        
        System.out.println("finished reading next node matrix2");
                
		// read order location data
        try{
            FileInputStream fstream = new FileInputStream("input/orders");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            	                    
            while ((strLine = br.readLine()) != null)   { // read file line by line
                    String[] data = strLine.split("\t");
                    
                    // convert a string of service time into double number
                    Integer zipcode = Integer.parseInt(data[0]);
                    Double lat = Double.parseDouble(data[1]);
                    Double lng = Double.parseDouble(data[2]);
                    
                    coordinate tmp = new coordinate(lat, lng);
                    tmp.setZip(zipcode);
                    
                    orders.add(tmp);
            }
            in.close(); //Close the input stream
	    } catch (Exception e){//Catch exception if any
	            System.err.println("Error: " + e.getMessage());
	    }
        
        System.out.println("finished reading orders data");
        
        // match each order with a node in distance matrix
        System.out.println("starting order matching");
        for(coordinate o : orders) {
        	double distance = Double.MAX_VALUE;
        	double lat = Double.MAX_VALUE;
        	double lng = Double.MAX_VALUE;
			long start = System.nanoTime();
        	for(coordinate s : node_lookup.get(o._zipcode)) {
        		double tmp_distance = distance(o._latitude, o._longitude, s._latitude, s._longitude);
        		if(tmp_distance < distance) {
        			distance = tmp_distance;
        			lat = s._latitude;
        			lng = s._longitude;
        			o._id = s._id;
        		}
        	}
			long end = System.nanoTime();
			//System.out.println(end-start);
        	//System.out.println(o._latitude +" "+ o._longitude + " "+  lat + " " + lng + " " +distance);
        }
        
        System.out.println("finish order matching");
        
        // calculate distance and time between orders
        for(coordinate origin : orders) {
        	for(coordinate destination : orders) {
        		int origin_node_id = origin._id;
        		int destination_node_id = destination._id;        		
        		int origin_node_index = id_to_index.get(origin_node_id);
        		int destination_node_index = id_to_index.get(destination_node_id);
        		
        		if(origin_node_id == destination_node_id) continue;
        		
        		System.out.print("from " + origin_node_id + " (index " + origin_node_index +")" + " to " + destination_node_id + " (index " + destination_node_index +")" + ": ");        		
        		
    			//long start = System.nanoTime();
        		
        		if(next_node_matrix2[origin_node_index][destination_node_index] == -1) {
        			//System.out.println("no feasible route, from index " + origin_node_index + " to index " + destination_node_index + " please revise map matching for the order");
        			continue;
        		}
        		
        		int start_node_index = origin_node_index;
        		int end_node_index = next_node_matrix2[origin_node_index][destination_node_index] - 1;
        		int start_node_id = origin_node_id;
        		int end_node_id = index_to_id.get(end_node_index);
        		        		
        		if(!links.containsKey(start_node_id)) {
        			System.out.println("missing link from start node " + start_node_id + "(index " + start_node_index + ")" + " to end node " + end_node_id + "(index " + end_node_index + ")");
        			continue;
        		} else if(!links.get(start_node_id).containsKey(end_node_id)) {
        			System.out.println("missing link from start node " + start_node_id + "(index " + start_node_index + ")" + " to end node " + end_node_id + "(index " + end_node_index + ")");
        			continue;
        		}
        		
        		double distance = links.get(start_node_id).get(end_node_id)._distance;
        		double time = links.get(start_node_id).get(end_node_id)._time;
        		
        		while(end_node_index != destination_node_index){
        			start_node_index = end_node_index;
        			end_node_index = next_node_matrix2[end_node_index][destination_node_index] - 1;
        			start_node_id = index_to_id.get(start_node_index);
        			end_node_id = index_to_id.get(end_node_index);
        			
            		if(!links.containsKey(start_node_id)) {
            			System.out.println("missing link from start node " + start_node_id + "(index " + start_node_index + ")" + " to end node " + end_node_id + "(index " + end_node_index + ")");
            			continue;
            		} else if(!links.get(start_node_id).containsKey(end_node_id)) {
            			System.out.println("missing link from start node " + start_node_id + "(index " + start_node_index + ")" + " to end node " + end_node_id + "(index " + end_node_index + ")");
            			continue;
            		}
        			
        			distance = distance + links.get(start_node_id).get(end_node_id)._distance;
        			time = time + links.get(start_node_id).get(end_node_id)._time;
        		}
        		
    			//long end = System.nanoTime();
    			//System.out.println(end-start);
        		
            	System.out.println("distance is " + distance + " time is " + time);
        	}
        }
        
/*
        // read path matrix file
        try{
            BufferedReader br = new BufferedReader(new FileReader("input/path_matrix.csv"));            
            String strLine = br.readLine();
            int i = 0;
            	                    
            while ((strLine = br.readLine()) != null)   { // read file line by line
            		path_matrix.put(nodes.get(i)._id, new HashMap<Integer, LinkedList<Integer>>());
                    String[] data = strLine.split(",");
                    
                    for(int j = 0; j < data.length; j++) {                    	
                    	if(data[j].equals("NA")) continue;
                    	//System.out.println("test1");
                    	//System.out.println(data[j]);
                    	data[j] = data[j].replace("\"","");
                    	String[] data2 = data[j].split("\\$");
                    	//System.out.println(data2.length);
                    	//System.out.println(data2[0]);
                    	if(data2.length < 2) continue;
                    	                    		
                    	LinkedList<Integer> path = new LinkedList<Integer>();
                    	for(int k = 0; k < data2.length; k++) {
                    		//System.out.println(data2.length);
                    		//System.out.println("test2");
                    		//System.out.println(data2[k]);
                     		Integer node = Integer.parseInt(data2[k]);
                    		//System.out.println("test3");
                    		path.add(node);
                    	}
                    	//System.out.println("test4");
                    	                    	
                    	path_matrix.get(nodes.get(i)._id).put(nodes.get(j)._id, path);
                    }
                    i++;
            }
           
            br.close();
	    } catch (Exception e){//Catch exception if any
	            System.err.println("Error: " + e.getMessage());
	    }
        
        System.out.println("finished reading path matrix...");               
        
        // calculate driving distance and time for shortest path of every pair of nodes
        for(int i : path_matrix.keySet()) {
        	for(int j : path_matrix.get(i).keySet()) {
        		LinkedList<Integer> path = path_matrix.get(i).get(j);
        		double distance = 0;
        		double time = 0;
        		long start = System.nanoTime();
        		for(int k = 0; k <  path.size() - 1; k++) {
        			distance = distance + links.get(path.get(k)).get(path.get(k+1))._distance;
        			time = time + links.get(path.get(k)).get(path.get(k+1))._time;
        		}
        		long end = System.nanoTime();
        		System.out.println(end - start);
        		//System.out.print(Math.round(distance) + "$" + Math.round(time) + " ");
        	}
        	System.out.println();
        }
        
        System.err.println("stop");                        
        
        // read distance matrix file
        try{
            BufferedReader br = new BufferedReader(new FileReader("input/distance_matrix.csv"));            
            String strLine;
            int i = 0;
            	                    
            while ((strLine = br.readLine()) != null)   { // read file line by line
                	int row_id = nodes.get(i)._id;
            		distance_matrix.put(row_id, new HashMap<Integer, Double>());
                    String[] data = strLine.split(",");
                    
                    for(int j = 0; j < data.length; j++) {
                    	if(data[j].equals("NA")) continue;
                    	
                    	Double distance = Double.parseDouble(data[j]);
                    	
                    	int column_id = nodes.get(j)._id;
                    	distance_matrix.get(row_id).put(column_id, distance);
                    }
                    i++;
            }
           
            br.close();
	    } catch (Exception e){//Catch exception if any
	            System.err.println("Error: " + e.getMessage());
	    }
        
        // lookup distances between nodes
        for(coordinate origin : orders) {
        	for(coordinate destination : orders) {
    			long start = System.nanoTime();
    			distance_matrix.get(origin._id).get(destination._id);
    			long end = System.nanoTime();
    			System.out.println(end-start);
        	}
        }
		

		//Date date = new Date();
		//System.out.println("time before reading orders is "+df.format(date));
        try{
            FileInputStream fstream = new FileInputStream("input/orders");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            	                    
            while ((strLine = br.readLine()) != null)   { // read file line by line
                    String[] data = strLine.split("\t");
                    
                    // convert a string of service time into double number
                    Double lat = Double.parseDouble(data[1]);
                    Double lng = Double.parseDouble(data[2]);
                    
                    coordinate tmp = new coordinate(lat, lng);
                    
                    orders.add(tmp);
            }
            in.close(); //Close the input stream
	    } catch (Exception e){//Catch exception if any
	            System.err.println("Error: " + e.getMessage());
	    }
        
        try{
            FileInputStream fstream = new FileInputStream("input/nodes");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            	                    
            while ((strLine = br.readLine()) != null)   { // read file line by line
                    String[] data = strLine.split("\t");
                    
                    // convert a string of service time into double number
                    Double lat = Double.parseDouble(data[1]);
                    Double lng = Double.parseDouble(data[2]);
                    
                    coordinate tmp = new coordinate(lat, lng);
                    
                    nodes.add(tmp);
            }
            in.close(); //Close the input stream
	    } catch (Exception e){//Catch exception if any
	            System.err.println("Error: " + e.getMessage());
	    }
      
        try{
            BufferedReader br = new BufferedReader(new FileReader("input/segments.csv"));            
            String strLine = br.readLine(); //remove the header line before reading
            	                    
            while ((strLine = br.readLine()) != null)   { // read file line by line
                    String[] data = strLine.split(",");
                    
                    // convert a string of service time into double number
                    Double lat1 = Double.parseDouble(data[3]);
                    Double lng1 = Double.parseDouble(data[2]);
                    Double lat2 = Double.parseDouble(data[5]);
                    Double lng2 = Double.parseDouble(data[4]);
                    Integer l_zip = Integer.parseInt(data[0]);
                    Integer r_zip = Integer.parseInt(data[1]);

                    //System.out.println("check point1");
                    segment tmp = new segment(lat1, lng1, lat2, lng2);
                    tmp.setZip(l_zip, r_zip);
                    //System.out.println("check point2");
                    segments.add(tmp);
            }           
            br.close();
	    } catch (Exception e){//Catch exception if any
	            System.err.println("Error: " + e.getMessage());
	    }
        

        try{
            BufferedReader br = new BufferedReader(new FileReader("input/segments.csv"));            
            String strLine = br.readLine(); //remove the header line before reading
            	                    
            while ((strLine = br.readLine()) != null)   { // read file line by line
                    String[] data = strLine.split(",");
                    
                    // convert a string of service time into double number
                    Double lat1 = Double.parseDouble(data[3]);
                    Double lng1 = Double.parseDouble(data[2]);
                    Double lat2 = Double.parseDouble(data[5]);
                    Double lng2 = Double.parseDouble(data[4]);
                    Integer l_zip = Integer.parseInt(data[0]);
                    Integer r_zip = Integer.parseInt(data[1]);

                    //System.out.println("check point1");
                    segment tmp = new segment(lat1, lng1, lat2, lng2);
                    tmp.setZip(l_zip, r_zip);
                    

                	if(!segments_lookup.containsKey(l_zip)) {
                		segments_lookup.put(l_zip, new LinkedList<segment>());                		
                	}
                	segments_lookup.get(l_zip).add(tmp);
                	if(r_zip != l_zip) {
                    	if(!segments_lookup.containsKey(r_zip)) {
                    		segments_lookup.put(r_zip, new LinkedList<segment>());                		
                    	}
                    	segments_lookup.get(r_zip).add(tmp);
                	}                	
            }           
            br.close();
	    } catch (Exception e){//Catch exception if any
	            System.err.println("Error: " + e.getMessage());
	    }
        
        for(node n : nodes) {
        	Double lat = Math.round(n._coordinate._latitude * 1000)/1000.0;
        	Double lng = Math.round(n._coordinate._longitude * 1000)/1000.0;
        	if(!node_lookup.containsKey(lat)) {
        		node_lookup.put(lat, new HashMap<Double, LinkedList<node>>());
        	}
        	if(!node_lookup.get(lat).containsKey(lng)) {
        		node_lookup.get(lat).put(lng, new LinkedList<node>());        			
        	}
        	node_lookup.get(lat).get(lng).add(n);
        	System.out.println("latitude is "+ lat + " longitude is " + lng);
        	System.out.println("node id is " + n._id + " latitude is " + n._coordinate._latitude + " longitude is " + n._coordinate._longitude);
        
        }
        
		//date = new Date();
		//System.out.println("time before reading node list is "+df.format(date));
        
        try{
            BufferedReader br = new BufferedReader(new FileReader("input/nodes.csv"));            
            String strLine = br.readLine(); //remove the header line before reading
            	                    
            while ((strLine = br.readLine()) != null)   { // read file line by line
                    String[] data = strLine.split(",");
                    
                    // convert a string of service time into double number
                    Double lat = Double.parseDouble(data[1]);
                    Double lng = Double.parseDouble(data[0]);
                                        
                    coordinate tmp = new coordinate(lat, lng);
                    
                    nodes.add(tmp);
            }
           
            br.close();
	    } catch (Exception e){//Catch exception if any
	            System.err.println("Error: " + e.getMessage());
	    }
        
		date = new Date();
		System.out.println("time before segmenting nodes is "+df.format(date));
        
        for(node n : nodes) {
        	Double lat = Math.round(n._coordinate._latitude * 1000)/1000.0;
        	Double lng = Math.round(n._coordinate._longitude * 1000)/1000.0;
        	if(!node_lookup.containsKey(lat)) {
        		node_lookup.put(lat, new HashMap<Double, LinkedList<node>>());
        	}
        	if(!node_lookup.get(lat).containsKey(lng)) {
        		node_lookup.get(lat).put(lng, new LinkedList<node>());        			
        	}
        	node_lookup.get(lat).get(lng).add(n);
        	System.out.println("latitude is "+ lat + " longitude is " + lng);
        	System.out.println("node id is " + n._id + " latitude is " + n._coordinate._latitude + " longitude is " + n._coordinate._longitude);
        
        }
        
        
        for(coordinate r : orders) {
        	Double lat = Math.round(r._latitude * 1000)/1000.0;
        	Double lng = Math.round(r._longitude * 1000)/1000.0;

        	if(!node_lookup.containsKey(lat)) {
        		System.out.println("doesn't contain the latitude value " + lat);
        		continue;
    		}
        	if(!node_lookup.get(lat).containsKey(lng)) {
        		System.out.println("doesn't contain the longitude value " + lng);
        		continue;
        	}
    		LinkedList<node> candidates = node_lookup.get(lat).get(lng);
        	double distance = Double.MAX_VALUE;
			long start = System.nanoTime();
        	for(node node : candidates) {
        		double tmp_distance = distance(r._latitude, r._longitude, node._coordinate._latitude, node._coordinate._longitude);
        		if(tmp_distance < distance) {
        			distance = tmp_distance;
        		}
        	}
			long end = System.nanoTime();
			System.out.println(end-start);

        }
        
		date = new Date();
		System.out.println("time before reading distance matrix is "+df.format(date));
        

        
		date = new Date();
		System.out.println("time before measuring speed is "+df.format(date));
        
        for(node n : nodes) {
        	for(node m : nodes) {
        		if(n._coordinate == m._coordinate) {
        			long start = System.nanoTime();
        			Double distance = distance_matrix.get(n._id).get(m._id);
        			long end = System.nanoTime();
        			System.out.println(end-start);
        		}
        	}
        }
        
        for(coordinate order : orders) {
        	double distance = Double.MAX_VALUE;
			//long start = System.nanoTime();
        	for(coordinate node : nodes) {
        		double tmp_distance = distance(order._latitude, order._longitude, node._latitude, node._longitude);
        		if(tmp_distance < distance) {
        			distance = tmp_distance;
        		}
        	}
			//long end = System.nanoTime();
			//System.out.println(end-start);
        	System.out.println(order._latitude +" "+ order._longitude + " "+ distance);
        }
*/
	}
	
    public static double distance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;
        return dist;
    }
    
    public static double plDist(double x, double y, double x1, double y1, double x2, double y2) {
        Double A = x - x1;
        Double B = y - y1;
        Double C = x2 - x1;
        Double D = y2 - y1;

        Double dot = A*C + B*D;
        Double sqLen = C*C + D*D;
        Double param = dot / sqLen;
        
        Double xx;
        Double yy;

        if (param < 0 || ((x1 == x2) && (y1 == y2))) {
            xx = x1;
            yy = y1;
        } else if (param > 1) {
            xx = x2;
            yy = y2;
        } else {
            xx = x1 + param*C;
            yy = y1 + param*D;
        }

        Double dx = x - xx;
        Double dy = y - yy;

        return Math.sqrt(dx*dx + dy*dy);
    }
}