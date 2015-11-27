package user.ihm.panels;

import user.ihm.UserIHM;
import user.ihm.enums.Etat;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;
import javax.swing.JTextField;

import java.awt.Color;

public class PanelSubscribe extends JPanel {
	private JTextField textFieldId;
	private JTextField textFieldMdp;
	private JLabel lblSignin,lblMotDePasse,lblIdentifiant,labelErreur;
	private JButton btnValider;
	protected UserIHM modele;
	private JTextField textFieldNom;
	private JTextField textFieldPrenom;
	private JTextField textFieldLoc;

	/**
	 * Create the panel.
	 */
	public PanelSubscribe(UserIHM m) {	
		modele = m;
		setLayout(null);
		lblSignin = new JLabel("S'inscrire :");
		lblSignin.setBounds(10, 11, 169, 14);
		add(lblSignin);
		
		JButton btnRetour = new JButton("Retour");
		btnRetour.setBounds(109, 214, 80, 23);
		add(btnRetour);
		
		lblIdentifiant = new JLabel("Identifiant :");
		lblIdentifiant.setBounds(30, 65, 131, 14);
		add(lblIdentifiant);
		
		lblMotDePasse = new JLabel("Mot de passe :");
		lblMotDePasse.setBounds(30, 90, 131, 14);
		add(lblMotDePasse);
		
		textFieldId = new JTextField();
		textFieldId.setBounds(194, 62, 235, 20);
		add(textFieldId);
		textFieldId.setColumns(10);
		
		textFieldMdp = new JTextField();
		textFieldMdp.setBounds(194, 87, 235, 20);
		add(textFieldMdp);
		textFieldMdp.setColumns(10);
		
		btnValider = new JButton("Valider");
		btnValider.setBounds(10, 214, 85, 23);
		add(btnValider);
		
		labelErreur = new JLabel("");
		labelErreur.setForeground(Color.RED);
		labelErreur.setBounds(30, 36, 399, 14);
		add(labelErreur);
		
		textFieldNom = new JTextField();
		textFieldNom.setColumns(10);
		textFieldNom.setBounds(194, 113, 235, 20);
		add(textFieldNom);
		
		JLabel lblNom = new JLabel("Nom :");
		lblNom.setBounds(30, 116, 131, 14);
		add(lblNom);
		
		JLabel lb1Prenom = new JLabel("Pr�nom :");
		lb1Prenom.setBounds(30, 141, 131, 14);
		add(lb1Prenom);
		
		textFieldPrenom = new JTextField();
		textFieldPrenom.setColumns(10);
		textFieldPrenom.setBounds(194, 138, 235, 20);
		add(textFieldPrenom);
		
		JLabel label = new JLabel("Localisation :");
		label.setBounds(30, 169, 131, 14);
		add(label);
		
		textFieldLoc = new JTextField();
		textFieldLoc.setColumns(10);
		textFieldLoc.setBounds(194, 166, 235, 20);
		add(textFieldLoc);
		
		btnRetour.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modele.changerPanel(Etat.Menu);
			}
		});

		btnValider.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modele.actionInscription(textFieldId.getText(),textFieldMdp.getText(),textFieldNom.getText(),textFieldPrenom.getText(),textFieldLoc.getText());
			}
		});	
	}


	public void afficherErreurEssai() {
		labelErreur.setText("Erreur : identifiant d�j� existant");
		razChampsSaisie();
	}
	
	public void remiseAZero(){
		labelErreur.setText("");
		btnValider.setEnabled(true);
		textFieldId.setEnabled(true);
		textFieldMdp.setEnabled(true);
		razChampsSaisie();
	}
	
	public void razChampsSaisie() {
		for (Component C : this.getComponents()){
			if(C instanceof JTextField){
				((JTextComponent) C ).setText("");
			}
		}
	}
}
