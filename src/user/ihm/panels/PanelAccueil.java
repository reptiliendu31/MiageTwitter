package user.ihm.panels;

import user.ihm.UserIHM;
import user.ihm.enums.Etat;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class PanelAccueil extends JPanel {

	protected UserIHM modele;
	private PanelAccueil myself = this;
	/**
	 * Create the panel.
	 */
	public PanelAccueil(UserIHM m) {		
		modele = m;
		
		setLayout(null);
		JLabel lblBienvenue = new JLabel("Bienvenue ! Choisissez une action :");
		lblBienvenue.setBounds(10, 11, 169, 14);
		add(lblBienvenue);
		
		JButton btnConnexion = new JButton("Se connecter");
		btnConnexion.setBounds(20, 46, 420, 23);
		add(btnConnexion);
		
		JButton btnQuitter = new JButton("Quitter");
		btnQuitter.setBounds(10, 189, 67, 23);
		add(btnQuitter);
		
		JButton btnInscription = new JButton("S'inscrire");
		btnInscription.setBounds(20, 80, 420, 23);
		add(btnInscription);
				
		btnInscription.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modele.changerPanel(Etat.Subscribe);
			}
		});
		
		btnConnexion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modele.changerPanel(Etat.Login);
			}
		});
		
		btnQuitter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modele.actionDisconnect();
				System.exit(0);
			}
		});

	}

}
