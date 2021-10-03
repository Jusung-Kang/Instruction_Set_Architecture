/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isa_project;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Jusung Kang
 */
public class Isa_project {
    //global variable
    public static String ISA[] = new String[100]; //array for file contents
    public static int index = 0;
    public static String var[] = new String[20]; //array for variable name
    public static int v1 = 0;
    public static float data[] = new float[10]; //array for data
    public static int d1=0;
    public static String memory[] = new String[50]; //actual memory
    public static int mm = 0;
    public static String middle[][] = new String[20][4]; //local array for middle block
    public static int m1 = 0, m2 = 0;
    public static float DTA990[] = new float[990];
    public static int d2 = 0;
    public static final int ZRO = 0;
    
    public static void main(String[] args) {
        //include file contents
        try{
            BufferedReader br = new BufferedReader(new FileReader("file.txt"));
            
            while(true){
                String line = br.readLine();
                if(line == null){
                    break;
                }//if
                ISA[index] = line;
                //System.out.println("ISA["+index+"]: "+ISA[index]);
                index++;
            }//while
            
        }catch(IOException io){
            
        }//catch
        
        //setup and arrange in bios and load on memory
        bios();
        
        //execute in cpu
        cpu();
    }//main
    
    public static void bios(){
        //check location of two END
        int end1 = 0, end2 = 0;
        for(int i = 0; i<index; i++){
            if(ISA[i].equals("END")){
                end1 = i;
                break;
            }//if
        }//for(i)
        for(int i = 0; i<index; i++){
            if(ISA[i].equals("END")){
                end2 = i;
            }//if
        }//for(i)
        //System.out.println("end1 and end2: "+end1+", "+" "+end2);
        
        //local array for top, middle, bottom
        String top[] = new String[30]; //local array for top block
        int t1 = 0;
        
        String bottom[] = new String[30]; //local array for bottom block
        int b1 = 0;
        
        //assign top block to top[]
        for(int i = 0; i<end1; i++){
            StringTokenizer stt = new StringTokenizer(ISA[i]);
            while(stt.hasMoreTokens()){
                top[t1] = stt.nextToken();
                //System.out.println("top["+t1+"]: "+top[t1]);
                t1++;
            }//while
        }//for(i)
        
        //select just variable name -> var[]
        for(int i = 0; i<t1; i++){
            if(i%4 == 1){
                var[v1] = top[i];
                //System.out.println("var["+v1+"]: "+var[v1]);
                v1++;
            }//if
        }//for (i)
        
        //assign middle block
        for(int j=end1+1; j<end2; j++){
           StringTokenizer stm = new StringTokenizer(ISA[j]);
           while(stm.hasMoreTokens()){
               middle[m1][m2] = stm.nextToken();
               //System.out.println("middel["+m1+"]["+m2+"]: "+middle[m1][m2]);
               m2++;
           }//while
           m1++; m2=0;
       }//for(j)     
        
        //assign bottom block to bottom[]
        for(int k = end2+1; k<index; k++){
            bottom[b1] = ISA[k];
            //System.out.println("botton["+b1+"]: "+bottom[b1]);
            b1++;
        }//for (k)
        
        //convert string to float on data
        for(int k = 0; k < b1; k++){
            data[d1] = Float.parseFloat(bottom[k]);
            System.out.println("data["+d1+"]: "+data[d1]);
            d1++;
        }//for(k)
        
        memory();
        
    }//bios
    
    public static void memory(){
        
        for(int i = 0; i<50; i++){
            memory[i] = "";
        }
        
        for(int i = 0; i<m1; i++){
            for(int j = 0; j<4; j++){
                if(middle[i][j] == null){
                    memory[mm] = memory[mm] + "000";
                }//if
                else{
                    memory[mm] = memory[mm] + encode(middle[i][j]);
                }//else
            }//for(j)
            //System.out.println("memory["+mm+"]: "+memory[mm]);
            mm++;
        }//for(i)
    }//memory
    
    public static String encode(String str){
        
        String result = "";
        
        if(str.equals("READ")){
            result = "+1";
        }//if 
        else if(str.equals("LABL")){
            result = "+2";
        }//else if
        else if(str.equals("GE")){
            result = "+3";
        }//else if
        else if(str.equals("ADD")){
            result = "+4";
        }//else if
        else if(str.equals("SUB")){
            result = "-4";
        }//else if
        else if(str.equals("PUTA")){
            result = "+5";
        }//else if
        else if(str.equals("GETA")){
            result = "-5";
        }//else if
        else if(str.equals("LOOP")){
            result = "+6";
        }//else if
        else if(str.equals("MOVE")){
            result = "-6";
        }//else if
        else if(str.equals("DIV")){
            result = "+7";
        }//else if
        else if(str.equals("PRNT")){
            result = "-8";
        }//else if
        else if(str.equals("STOP")){
            result = "-9";
        }//else if
        
        //variable encoding
        else if(str.equals(var[0])){
            result = "001";
        }
        else if(str.equals(var[1])){
            result = "002";
        }
        else if(str.equals(var[2])){
            result = "003";
        }
        else if(str.equals(var[3])){
            result = "004";
        }
        else if(str.equals(var[4])){
            result = "005";
        }
        else if(str.equals(var[5])){
            result = "006";
        }
        else if(str.equals(var[6])){
            result = "007";
        }
        
        //LABL position encoding
        else{
            result = "0" + str;
        }
        
        return result;
    }//encode
    
    public static void cpu(){
        //6step cpu execution
        int pc = 0;
        
        //instruction decode
        String instruction = "";
        String opcode = "";
        String ad1 = "";
        String ad2 = "";
        String ad3 = "";
        
        d1 = 0;
            float AVG = 0;
            float I = 0;
            float SUM = 0;
            float N = 0;
            float TMP = 0;
            final int ZRO = 0;
        
        
        while(!opcode.equals("-9")){
            
            
            
            //instruction fetch
            instruction = memory[pc];
            opcode = instruction.substring(0, 2);
            ad1 = instruction.substring(2, 5);
            ad2 = instruction.substring(5, 8);
            ad3 = instruction.substring(8, 11);
            
            System.out.println(opcode+" "+ad1+" "+ad2+" "+ad3);
            
            switch(opcode){
                //read
                case "+1":{ 
                    System.out.println("READ");
                    if(ad1.equals("005")){
                        N = data[d1];
                        System.out.println("N = "+N);
                    }
                    else{
                        TMP = data[d1];
                        System.out.println("TMP = "+TMP);
                    }
                    
                    d1++;
                    //pc++;
                    break;
                }//READ
                
                //labl
                case "+2":{
                    if(ad1.equals("020")){
                        System.out.println("LABL 20");
                    }
                    else if(ad1.equals("040")){
                        System.out.println("LABL 40");
                    }
                    else if(ad1.equals("050")){
                        System.out.println("LABL 50");
                    }

                    //pc++;
                    break;
                }//LABL
                
                //ge
                case "+3":{
                    System.out.println("GE");
                    if(TMP >= ZRO){
                        System.out.println("GE = "+TMP);
                        pc++;
                    }//else
                    else if(TMP < ZRO){
                        System.out.println("GE = "+TMP);
                    }//else
                    break;
                }//GE
                
                //add
                case "+4":{
                    System.out.println("ADD");
                    SUM =SUM + TMP;
                    System.out.println("SUM = "+SUM);
                }//ADD
                
                //sub
                case "-4":{
                    //System.out.println("SUB"); 
                    TMP = ZRO - TMP;
                    break;
                }//SUB
                
                //puta
                case "+5":{
                    System.out.println("PUTA");
                    DTA990[d2] = TMP;
                    System.out.println("d2 = "+d2);
                    System.out.println("DTA990 = "+DTA990[d2]);
                    d2++;
                    break;
                }//PUTA
                
                //geta
                case "-5":{
                    System.out.println("GETA");
                    TMP = DTA990[d2];
                    d2++;
                    break;
                }//GETA
                
                //loop
                case "+6":{
                    System.out.println("LOOP");
                    if(ad3.equals("020")){
                        if(d2 < N){
                            pc = pc-6;
                        }
                    }
                    else if(ad3.equals("050")){
                        if(d2 < N){
                            pc = pc-3;
                        }
                    }
                    break;
                }//LOOP
                
                //move
                case"-6":{
                    System.out.println("MOVE");
                    d2 = ZRO;
                    System.out.println("MOVE d2 = "+d2);
                    break;
                }//MOVE
                
                //div
                case "+7":{
                    System.out.println("DIV");
                    AVG = SUM / N;
                    System.out.println("AVG = "+AVG);
                    break;
                }//DIV
                
                //prnt
                case"-8":{
                    System.out.println("PRINT");
                    try {
                        BufferedWriter bw = new BufferedWriter(new FileWriter("File.txt", true));
                        bw.write("Result: "+AVG);
                        bw.close();
                    } catch (IOException ex) {
                    }//catch              
                    break;
                }//PRNT
                
                /*//stop
                case"-9":{
                    System.out.println("STOP");
                    System.exit(0);
                }//STOP*/
            }//switch
            System.out.println("PC = "+pc);
            System.out.println("");
            pc++;
        }//while
    }//cpu
        //1.instruction fetch     
        //2.instruction decode
        //3.operand fetch
        //4. execution 
        //5. store result 
        //6. fetch next instruction 
}//Main
