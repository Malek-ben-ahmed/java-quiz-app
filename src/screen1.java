import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.awt.event.ActionEvent;
public class screen1 extends JFrame {
	private JButton b;
	private JButton b1;
	private JPanel p1;
	private JPanel p2;
	private JPanel p3;
	private JLabel l1;
	private ButtonGroup group;
	private JComboBox cmb;
	private JRadioButton r1;
	private JRadioButton r2;
	private JRadioButton r3;
	private JRadioButton r4;
	private JRadioButton r5;
	private int i=1;
	private int score=0;
   public screen1() {
	   setTitle("Application Quiz");
	   setSize(400,300);
	   setDefaultCloseOperation(EXIT_ON_CLOSE);
	   JPanel p=new JPanel();
	   JLabel l=new JLabel("Catégorie:");
	   p.add(l);
	   cmb=new JComboBox();
	   cmb.addItem("Science");
	   cmb.addItem("Géographie");
	   cmb.addItem("Histoire");
	   p.add(cmb);
	   b=new JButton("Charger Questions");
	   p.add(b);
	   add(p,BorderLayout.NORTH);
	   setVisible(true);
	   p1=new JPanel();
	   l1=new JLabel();
	   add(p1,BorderLayout.CENTER);
	   p2=new JPanel(new GridLayout(5,1,10,10));
	   add(p2,BorderLayout.WEST);
	   p3=new JPanel();
	   b1=new JButton("Suivant");
	   p3.add(b1);
	   add(p3,BorderLayout.SOUTH);
	   b.addActionListener(new ActionListener() {
		   public void actionPerformed(ActionEvent e) {
			   i=1;
			   Affichagequestion(i);
			   Afficher_propositions(i);
		   }
	   });
	   b1.addActionListener(new ActionListener() {
		   public void actionPerformed(ActionEvent e) {
			   Reponse_correcte(i);
			   if (i==3) {
				  JOptionPane.showMessageDialog(b1,"Quiz terminé!Votre score:"+score+"/3 pour la catégorie"+""+cmb.getSelectedItem().toString());
				  System.exit(0);
			   }
			   else {
			   i=i+1;
			   p2.removeAll(); 
			   Affichagequestion(i);
			   Afficher_propositions(i);
			   }
		   }
		   });
	   
	   
   }
   
   public Connection connect_to_quizdb()throws SQLException{
	   String url="jdbc:mysql://localhost:3306/quiz_db";
	   String user="root";
	   String mdp="";
	   return DriverManager.getConnection(url,user,mdp);
   }
   private void Affichagequestion(int i) {
	   try(Connection conn=connect_to_quizdb()) {
		  String categorie=cmb.getSelectedItem().toString(); 
		  String sql="SELECT q.* FROM Questions q "
		           + "JOIN catégorie c ON q.Id_catégorie = c.Id_catégorie "
		           + "WHERE c.name_catégorie = ? AND q.Id_question = ?";
	      PreparedStatement st=conn.prepareStatement(sql);
	      st.setString(1,categorie);
	      st.setInt(2,i);
	      ResultSet rs = st.executeQuery();
	      if (rs.next()) {
	          String q = rs.getString("question_text");
	          l1.setText(q);
	          p1.add(l1);
	      } else {
	          JOptionPane.showMessageDialog(this, "Aucune question trouvée pour les paramètres donnés.");
	      }

	      }catch(SQLException e) {JOptionPane.showMessageDialog(this,"Erreur SQL : " + e.getMessage());}
   }
   private void Afficher_propositions(int i) {
	   try(Connection conn=connect_to_quizdb()){ 
		   String sql="select proposition_text from propositions where Id_question=?";
		   PreparedStatement stm=conn.prepareStatement(sql);
		   stm.setInt(1,i);
		   ResultSet rs=stm.executeQuery();
		   group = new ButtonGroup();
		   while(rs.next()) {
			   String p=rs.getString("proposition_text");
			   r1=new JRadioButton(p);
			   group.add(r1);
			   p2.add(r1);}}
		   catch(SQLException e) {JOptionPane.showMessageDialog(this,"ERROR:"+e.getMessage());}
		   
	   }
   public int Reponse_correcte(int i) {
	   try(Connection conn=connect_to_quizdb()){
		   String sql="select proposition_text from propositions where is_correct=? and Id_question=?";
		   PreparedStatement stm=conn.prepareStatement(sql);
		   stm.setInt(1,1);
		   stm.setInt(2,i);
		   ResultSet rs=stm.executeQuery();
		   String corr_response="";
		   if (rs.next()) {
			corr_response=rs.getString("proposition_text"); 
	        } else {
	            System.out.println("Aucune réponse correcte trouvée.");
		     }
		   String response="";
		   for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
			    JRadioButton button = (JRadioButton) buttons.nextElement();
			    if (button.isSelected()) {
			       response= button.getText();
			       System.out.println(response);
			        break;
			    }}
		   if(response.equals(corr_response)) { score++  ;};
		   }catch(SQLException e) {JOptionPane.showMessageDialog(this, "ERROR"+e.getMessage());};
		return score;
   }
   }

