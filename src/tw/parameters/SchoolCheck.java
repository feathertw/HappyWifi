package tw.parameters;

import java.util.ArrayList;
import java.util.List;

public class SchoolCheck {
	
	public static School school;
	private List<School> schoolList = new ArrayList<School>();
	
	public SchoolCheck(){
		
		schoolList.add(NCKU.getSchool());
		schoolList.add(NUTN.getSchool());	
		schoolList.add(STUT.getSchool());	
	}
	
	public School getKey(String SSID){
		
		for(School s : schoolList){
			for(String ssid : s.ssid){
//				if(SSID.equals(ssid))	return s;
				if(SSID.indexOf(ssid)!=-1)	return s;
			}
		}
		return null;
	}
	
	public String[] getSchoolName(){
		int size=schoolList.size();
		String item[]=new String[size];
		for(int i=0;i<size;i++){
			item[i]=schoolList.get(i).name;
		}
		return item;
	}
	public String[] getSchoolMail(){
		int size=schoolList.size();
		String item[]=new String[size];
		for(int i=0;i<size;i++){
			item[i]=schoolList.get(i).mail;
		}
		return item;
	}
}




