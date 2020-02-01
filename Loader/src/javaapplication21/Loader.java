package JavaApplication21;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Loader {

    public static Object relative(String inst)
    {
        Dictionary relative_address=new Hashtable();

        relative_address.put("AR","2");
        relative_address.put("A","4");
        relative_address.put("ALR","2");
        relative_address.put("AL","4");
        relative_address.put("BALR","2");
        relative_address.put("BAL","4");
        relative_address.put("BASR","2");
        relative_address.put("BAS","4");
        relative_address.put("BCTR","2");
        relative_address.put("BCT","4");
        relative_address.put("CR","2");
        relative_address.put("C","4");
        relative_address.put("CP","6");
        relative_address.put("CLR","2");
        relative_address.put("CL","4");
        relative_address.put("D","4");
        relative_address.put("DR","2");
        relative_address.put("LR","2");
        relative_address.put("L","4");
        relative_address.put("LA","4");
        relative_address.put("LTR","2");
        relative_address.put("LCR","2");
        relative_address.put("MVI","4");
        relative_address.put("MVC","6");
        relative_address.put("M","4" );
        relative_address.put("O","4");
        relative_address.put("OR","2");
        relative_address.put("SPM","2");
        relative_address.put("SLDA","4");
        relative_address.put("SR","2");
        relative_address.put("S","4");
        relative_address.put("SP","6");
        relative_address.put("SL","4");
        relative_address.put("LD","4");
        relative_address.put("B","4");
        relative_address.put("BR",2);
        relative_address.put("BH",4);
        relative_address.put("BL",4);
        relative_address.put("BNE","4");
        relative_address.put("DC",4);
        relative_address.put("DS",4);
        relative_address.put("START",0);
        relative_address.put("ENTRY",0);
        relative_address.put("EXTERN",0);
        relative_address.put("END",0);

        if(relative_address.get(inst)!=null)
            return relative_address.get(inst);
        else
        {
            System.out.println("DONE ! !");
            return -1;
        }


    }

    public static int getLength(File f){

        try
        {
            BufferedReader br=new BufferedReader(new FileReader(f));
            int length=0;
            String thisLine=" ";
            String s[];
            int lineCount=0;


            while(true)
            {
                thisLine=br.readLine();

                if(thisLine==null)
                    break;

                lineCount++;
                s=thisLine.split(" ");//for every line symbol instruction parameter



                if((s.length)>=3 || lineCount==1)//for start line & for line consiting of all three things
                {
                    length+=(int)relative(s[1]);
                }
                else
                {
                    length +=(int)relative(s[0]);
                }

            }

            return length;
        }
        catch(IOException e)
        {
            System.out.println(e);
            return 0;
        }
    }

    public static void main(String[] args) {

        try{
            File f=new File("C:\\Users\\RV\\Downloads\\loader_program.txt");
            BufferedReader br=new BufferedReader(new FileReader(f));



            PrintWriter esd=new PrintWriter(new File("ESD.txt"));
            PrintWriter txt=new PrintWriter(new File("TXT.txt"));
            PrintWriter rld=new PrintWriter(new File("RLD.txt"));

            esd.println("NAME OF SYMBOL\tTYPE\tID\tRELATIVE ADDRESS\tLENGTH");//All headings have been printed
            txt.println("RELATIVE ADDRESS\t    CONTENT");
            rld.println("ESD ID\t LENGTH\tFLAG(+/-)\tRELATIVE ADDRESS");

            System.out.println("ESD,TXT and RLD have been created successfully ! ");
            String thisLine=" ";

            ArrayList<String> symbolArray = new ArrayList<>();


            int lineCount=0,length_program=0;

            Dictionary relativeAddress = new Hashtable();
            Dictionary typeOfSymbol = new Hashtable();
            Dictionary line_rel = new Hashtable();

            String s[];
            int id=1;


            int lengthOfCode = getLength(f);
            lengthOfCode++;

            //***********************************ESD CODE****************************************************

            while(true)
            {

                thisLine=br.readLine();

                if(thisLine==null)
                    break;

                lineCount++;
                s=thisLine.split(" ");


                if(s[0].equals("ENTRY"))
                {
                    String s2[] = s[1].split(",");

                    for(int i=0;i<s2.length;i++)
                    {
                        typeOfSymbol.put(s2[i],1);
                        relativeAddress.put(s2[i],0);
                        symbolArray.add(s2[i]);


                    }
                }
                if(s[0].equals("EXTERN"))
                {
                    String s2[] = s[1].split(",");

                    for(int i=0;i<s2.length;i++)
                    {
                        typeOfSymbol.put(s2[i],2);
                        relativeAddress.put(s2[i],0);
                        symbolArray.add(s2[i]);

                    }
                }
                if((s.length)>=3 || lineCount==1)
                {
                    relativeAddress.put(s[1], length_program);


                    if(s.length>=3 && (!s[0].equals("EXTERN")) && (!s[0].equals("ENTRY")) )
                    {
                        relativeAddress.put(s[0], length_program);//symbol and it's corresponding relative address is being stored

                        line_rel.put(lineCount,length_program);

                    }
                    length_program +=(int)relative(s[1]);

                    if(s[1].equals("START"))
                    {
                        typeOfSymbol.put(s[0],0);// 0 is for SD type;
                        // 1 is for LD type
                        // 2 is for ER type
                        relativeAddress.put(s[0],0);

                        symbolArray.add(s[0]);

                    }


                }
                else
                {
                    relativeAddress.put(s[0], length_program);
                    line_rel.put(lineCount,length_program);
                    length_program +=(int)relative(s[0]);

                }

            }
            //Code to print in ESD
            for(int i=0;i<symbolArray.size();i++)
            {
                if(i==0)
                {
                    esd.print(symbolArray.get(i)+"\t\tSD\t0"+id+"\t\t"+relativeAddress.get(symbolArray.get(i))+"\t\t  "+lengthOfCode);
                }
                else
                {
                    if((int)typeOfSymbol.get(symbolArray.get(i))==1)
                    {
                        esd.print(symbolArray.get(i)+"\t\tLD\t0"+id+"\t\t"+relativeAddress.get(symbolArray.get(i))+"\t\t  0");
                    }
                    else if((int)typeOfSymbol.get(symbolArray.get(i))==2)
                    {
                        id++;
                        esd.print(symbolArray.get(i)+"\t\tER\t0"+id+"\t\t"+relativeAddress.get(symbolArray.get(i))+"\t\t  0");
                    }
                }
                esd.println("");

            }
            esd.close();

            //***********************************TXT CODE****************************************************

            BufferedReader br3=new BufferedReader(new FileReader(f));
            String thisLine2=" ";
            String st[],x[],y[],z[];
            Dictionary txt_print_ra = new Hashtable();
            int ra_count=0;
            Dictionary txt_print_content = new Hashtable();
            int content_count=0;
            CharSequence seq1 = "+";
            CharSequence seq2 = "-";
            boolean b1=false,b2=false;
            int lineCount2=0;

            while(true)
            {

                thisLine2=br3.readLine();

                if(thisLine2==null)
                    break;

                lineCount2++;
                st=thisLine2.split(" ");

                if((st.length)>=3)
                {
                    b1=st[2].contains(seq1);
                    b2=st[2].contains(seq2);

                    if(st[1].equals("DC") )
                    {

                        x=st[2].split("[(]");

                        if(x.length==2 && b1==false && b2==false)
                        {
                            ra_count++;
                            content_count++;
                            y=x[1].split("[)]");

                            int content =(int)relativeAddress.get(y[0]);
                            int ra=(int)line_rel.get(lineCount2);//for line get from line count dictionary
                            int nextra=ra+3;
                            String rag=ra+"-"+nextra;

                            txt_print_ra.put(ra_count,rag);
                            txt_print_content.put(content_count,content);


                        }
                        else if(x.length==2 && b1==true && b2==false)//for+
                        {
                            ra_count++;
                            content_count++;
                            z=x[1].split("[+]");
                            int content1=(int)relativeAddress.get(z[0]);
                            y=z[1].split("[)]");
                            int content2=(int)relativeAddress.get(y[0]);

                            int content=content1+content2;
                            int ra=(int)line_rel.get(lineCount2);

                            int nextra=ra+3;
                            String rag=ra+"-"+nextra;
                            txt_print_ra.put(ra_count,rag);
                            txt_print_content.put(content_count,content);


                        }
                        else
                        {
                            ra_count++;
                            content_count++;
                            z=x[1].split("[-]");
                            int content1=(int)relativeAddress.get(z[0]);
                            y=z[1].split("[)]");
                            int content2=(int)relativeAddress.get(y[0]);

                            int content=content1-content2;
                            int ra=(int)line_rel.get(lineCount2);

                            int nextra=ra+3;
                            String rag=ra+"-"+nextra;
                            txt_print_ra.put(ra_count,rag);
                            txt_print_content.put(content_count,content);


                        }
                    }
                }
                else if(st.length==2)
                {

                    b1=st[1].contains(seq1);
                    b2=st[1].contains(seq2);
                    if(st[0].equals("DC") )
                    {

                        x=st[1].split("[(]");

                        if(x.length==2&& b1==false && b2==false)
                        {
                            ra_count++;
                            content_count++;
                            y=x[1].split("[)]");

                            int content =(int)relativeAddress.get(y[0]);

                            int ra=(int)line_rel.get(lineCount2);
                            int nextra=ra+3;
                            String rag=ra+"-"+nextra;
                            txt_print_ra.put(ra_count,rag);
                            txt_print_content.put(content_count,content);


                        }
                        else if(x.length==2&& b1==true && b2==false)
                        {
                            ra_count++;
                            content_count++;
                            z=x[1].split("[+]");
                            int content1=(int)relativeAddress.get(z[0]);
                            y=z[1].split("[)]");
                            int content2=(int)relativeAddress.get(y[0]);
                            int content=content1+content2;


                            int ra=(int)line_rel.get(lineCount2);
                            int nextra=ra+3;
                            String rag=ra+"-"+nextra;
                            txt_print_ra.put(ra_count,rag);
                            txt_print_content.put(content_count,content);


                        }
                        else
                        {
                            ra_count++;
                            content_count++;
                            z=x[1].split("[-]");
                            int content1=(int)relativeAddress.get(z[0]);
                            y=z[1].split("[)]");
                            int content2=(int)relativeAddress.get(y[0]);
                            int content=content1-content2;
                            int ra=(int)line_rel.get(lineCount2);
                            int nextra=ra+3;
                            String rag=ra+"-"+nextra;
                            txt_print_ra.put(ra_count,rag);
                            txt_print_content.put(content_count,content);


                        }
                    }
                }
            }
            for(int k=1;k<=content_count;k++)
            {
                txt.println("   "+txt_print_ra.get(k)+"\t\t\t\t"+txt_print_content.get(k));
            }

//*****************************************************************RLD CODE**************************************************************//


            File f2=new File("C:\\Users\\RV\\Downloads\\loader_program.txt");
            BufferedReader br2=new BufferedReader(new FileReader(f2));

            File f1=new File("ESD.txt");
            BufferedReader br1=new BufferedReader(new FileReader(f1));
            String[] v=new String[5];
            String[] w= new String[5];
            String[] r= new String[5];

            Dictionary flagOfSymbol = new Hashtable();


            String c=new String();

            while((thisLine=br2.readLine()) != null){
                int reladd=0;
                String[] t=thisLine.split(" ");

                if(t[0].equalsIgnoreCase("DC")||t[1].equalsIgnoreCase("DC")){

                    for (String p : t){
                        if(p.contains("("))
                            r = p.split("[(]");
                    }
                    for (String p : r){
                        if(p.contains(")"))
                            w = p.split("[)]");
                    }


                    if(w[0].contains("+")){
                        v = w[0].split("[+]");
                        for(int j=0 ; j<v.length  ; j++){
                            flagOfSymbol.put(v[j], "+");
                        }

                    }
                    else if(w[0].contains("-")){
                        v = w[0].split("[-]");
                        for(int j=0 ; j<v.length  ; j++){
                            flagOfSymbol.put(v[0], "+");
                            flagOfSymbol.put(v[1], "-");

                        }
                    }

                    else{
                        v[0] = w[0];
                        flagOfSymbol.put(v[0], "+");
                    }


                    for(int j=0 ; j<v.length  ; j++){
                        if(v[j]!=null){
                            if(v.length==2 &&(((int)typeOfSymbol.get(v[0]))==0|| ((int)typeOfSymbol.get(v[0]))==1) && (((int)typeOfSymbol.get(v[1]))==0|| ((int)typeOfSymbol.get(v[1])==1))){
                                j++;
                                continue;
                            }
                            else{
                                while((thisLine=br1.readLine()) != null){
                                    String[] a=thisLine.split("\\t");
                                    if(a[0].equals(v[j])){
                                        c=a[3];
                                    }
                                }
                                rld.println(c+"\t\t  4\t\t  " + flagOfSymbol.get(v[j]) +"\t\t   "+ reladd);
                            }

                        }

                    }
                    reladd+=4;
                }
            }

            esd.close();
            txt.close();
            rld.close();
            br.close();
        }
        catch(IOException e)
        {
            System.out.println(e);
        }
    }
}