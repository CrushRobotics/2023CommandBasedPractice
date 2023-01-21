package systems.subsystems;

import systems.Resources;

public class RPID {

    private double p,i,d;
    private double minIn = 0.0;
    private double maxIn = 0.0;
    private double minOut = 0.0;
    private double maxOut = 1.0;
    private boolean continuous = false;


    private double setPoint = 0.0;
    private double prevError = 0.0;
    private double totalError = 0.0;
    private double currentError = 0.0;
    private double lastInput = 0.0;
    private double acceptableRange = 0.0;

    private double dt;

    public RPID(double p, double i, double d){
        setPID(p,i,d);
        dt=0.01;
    } 
    
    public RPID(double p, double i, double d, double dt){
        setPID(p,i,d);
        this.dt=dt;
    }


    public double crunch(double input){
        double pValue, iValue, dValue;
        lastInput = input;
        currentError = setPoint - input;
        if(continuous){
            if(Math.abs(currentError) > (maxIn - minIn)/2){
                currentError  = currentError>0? currentError-maxIn+minIn : currentError+maxIn-minIn;
            }
        }

        if(currentError * p < maxOut && currentError * p > minOut){
            totalError+=currentError;
        }else{
            totalError=0;
        }

        //pValue = Math.abs(currentError) < acceptableRange ? 0: p * currentError;
        pValue = p * currentError;
        iValue = i * totalError * dt;
        dValue = d * (currentError - prevError)/dt;


        prevError = currentError;
        return Resources.limit(pValue + iValue + dValue,minOut,maxOut);
    }

    public void setPID(double p, double i, double d){
        this.p=p;
        this.i=i;
        this.d=d;
    }

    public void resetAll(){
        totalError=0;
        prevError=0;
    }

    public boolean onTarget(){
        return Math.abs(lastInput - setPoint) < acceptableRange;
    }

    public double getMinIn() {
        return minIn;
    }

    public void setInputRange(double minIn, double maxIn) {
        this.minIn = minIn;
        this.maxIn = maxIn;
    }

    public double getMaxIn() {
        return maxIn;
    }


    public double getMinOut() {
        return minOut;
    }

    public void setOutputRange(double minOut, double maxOut) {
        this.minOut = minOut;
        this.maxOut= maxOut;
    }

    public double getMaxOut() {
        return maxOut;
    }

    public boolean isContinuous() {
        return continuous;
    }

    public void setContinuous(boolean continuous) {
        this.continuous = continuous;
    }

    public double getSetPoint() {
        return setPoint;
    }

    public void setSetPoint(double setPoint) {
        this.setPoint = setPoint;
    }

    public double getAcceptableRange() {
        return acceptableRange;
    }

    public void setAcceptableRange(double acceptableRange) {
        this.acceptableRange = acceptableRange;
    }
}
