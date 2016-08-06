package test;

public class coordinate {
	double _latitude;
	double _longitude;
	int _zipcode;
	int _id;
	
	public coordinate(Double latitude, Double longitude) {
		_latitude = latitude;
		_longitude = longitude;
	}
	
	public void setZip(Integer zipcode){
		_zipcode = zipcode;
	}
	
	public void setId(Integer id) {
		_id = id;
	}
}
