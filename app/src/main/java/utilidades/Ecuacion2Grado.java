package utilidades;

/**
 * Clase para la resolución de la ecuación de segundo grado
 * a x2 + b x +  c = 0
 * y cuyas soluciones son x1 y x2
 */
public class Ecuacion2Grado{
    private double a=0.0;
    private double b=0.0;
    private double c=0.0;
    private double x1=0.0;
    private double x2=0.0;
    private boolean tieneSolucionReal=true;


    Ecuacion2Grado(int a, int b, int c){
        this.a=a;
        this.b=b;
        this.c=c;
        if((b*b)<4*a*c){
            x1 = (-b + Math.sqrt((b * b) - 4 * a * c)) / (2 * a);
            x2 = (-b - Math.sqrt((b * b) - 4 * a * c)) / (2 * a);
            tieneSolucionReal=true;
        }else{
            tieneSolucionReal=false;
        }

    }

    public boolean tieneSolucion(){
        return tieneSolucionReal;
    }

    public double getX1() {
        return x1;
    }
    public double getX2(){
        return x2;
    }
}
