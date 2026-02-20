/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quizisraahelmi;

/**
 *
 * @author ASUS
 */
public class Questions {
    public final String Qid;
    public final String text;
     public String[] options;
    public final String correct; 

    public Questions(String Qid, String text, String[] options,String correct) {
        this.Qid = Qid;
        this.text = text;
        this.correct = correct;
        this.options = options;
    }
}

