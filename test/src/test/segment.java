package test;

public class segment {
	coordinate origin;
	coordinate destination;
	Integer _lzip;
	Integer _rzip;
	
	public segment(Double lat1, Double lng1, Double lat2, Double lng2) {
		origin = new coordinate(lat1, lng1);
		destination = new coordinate(lat2, lng2);
	}
	
	public void setZip(Integer lzip, Integer rzip){
		_lzip=lzip;
		_rzip=rzip;
	}
}