/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nintendods
 */
public class Equation {
    
    pair[] eq;
    int res;
    
    public Equation(String str){
     
        StringToEquation(str);
        
    }    

    private class operator{
        char op;
    
        private boolean  isCharOperator(char c){

            return switch(c){

                case '+' -> true;
                case '-' -> true;
                case '*' -> true;
                case '/' -> true;
                case '=' -> true;
                default -> false;    
            };
        }
        
        public boolean isoperator(){
            return isCharOperator(op);
        }
    
        public operator(char c){
            
            if( isCharOperator(c) ){
            
                op = c;
            }else{
            
                op = '@';           //'@' indica fim
            }
        }
        
        public boolean isInArray(operator o[]){
        
            for(operator k: o ){
            
                if( k.op == op ){
                    return true;
                }
            }
            return false;
        }
    }
    /*----------END OPERATOR CLASS--------------*/
    
    private class pair{

        int num;
        operator op;
        
        public pair(operator o, int n ){
            op = o;
            num = n;
        }
        
        
        public void print(){
        
            System.out.println("numero: "+num+"\n Operador: "+op.op);
        
        }
        
        /*
        Ex.:
            p0 = {7,*}
            p1 = {8,@}
            p0.solvepair(p1);
            p0 = {56,@} 
        */
        public void solvepair(pair p1){
                       
            num = switch (op.op) {
                case '+' -> p1.num + num;
                case '-' -> p1.num - num;
                case '*' -> p1.num * num;
                case '/' -> p1.num / num;
                default -> 0;                    
            };        
            op = p1.op;
        }
    }    
 
    /*----------END PAIR CLASS--------------*/
 
    //Delete the elemente of the eq at given index
    private void delElem(int idx){
            
        for(int j = idx;j < eq.length - 1;){
            
            eq[j] = eq[++j];
        }   
        eq = java.util.Arrays.copyOf(eq, eq.length - 1);
    }
    
    //Prints de equation
    public void printEq(){
    
        System.out.println("---------array--------");
        for(pair p: eq){
            p.print();
        }        
        System.out.println("--------end---------");
    }
    
    //Solves the equation
    public void Solve(){
        
        operator[] o2 = { new operator('+'),new operator('-')},
                   o1 = { new operator('*'),new operator('/')};
   
        SolvePriority(o1,eq.length - 1);

        SolvePriority(o2,eq.length - 1);
        res = eq[0].num;
    }
    
    private void SolvePriority(operator o[],int n){
    
        if( !eq[n].op.isInArray(o) ){
            
            if( n == 0 ){ return; }
            
            SolvePriority(o,--n);
            return;
        }

        eq[n].solvepair(eq[n + 1]);
        
        delElem(n + 1);

        if( --n == -1 ){ return; }
        SolvePriority(o,n);
    }
    
    private void append(pair p){
    
        int arrsize = eq == null ? 1 : eq.length + 1;
        pair[] tmp = new pair[ arrsize ];
        
        if( eq == null){
            tmp[0] = new pair(p.op,p.num);
            eq = tmp;
            return;
        }
        
        int i;
        for(i = 0;i < arrsize-1;++i ){
            tmp[i] = new pair(eq[i].op,eq[i].num);
        }
        
        tmp[i] = new pair(p.op,p.num);
        eq = tmp;
    }
    
    private void StringToEquation(String str){
    
        int StrIt = str.length() - 1;
        String tmp;
        char operator = 0;
        pair p;
        
        while(operator != '@'){
            
            //search for operators
            while( (str.charAt(StrIt) - '0' ) <= 9 && 
                   (str.charAt(StrIt) - '0' ) >= 0) {            
                if (--StrIt == -1){
                    ++StrIt;
                    break;
                }
            }
            
            if( StrIt == 0 ){
                operator = '@';
                tmp = str;
                
            }else{
                operator = str.charAt(StrIt);
                tmp = str.substring(StrIt + 1);
                str = str.substring(0,StrIt--);
            }
                        
            try{
            
                p = new pair( new operator(operator),Integer.parseInt(tmp) );
            }catch(Exception e){
            
                System.out.println(e);
                return;
            }
            append(p);
        }
    }
}
