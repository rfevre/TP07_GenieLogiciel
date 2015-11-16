import java.awt.* ;        // gestion des composants graphiques AWT
import java.awt.event.* ;  // gestion des evenements 

/** L'application qui sert a faire du dessin est une sorte de cadre (Frame). */
public class Paint extends Frame 
{
    /** Definition des outils disponibles */
    public static final int POINT=0, LINE=1, RECTANGLE=2 ;

    /** Definition du nom des outils (ce qui apparait sur les boutons correspondants) */
    public static final String [] OUTILS = {"Point", "Ligne", "Rectangle"} ;

    /** Definition des couleurs disponibles */
    public static final int VERT=0, ROUGE=1, JAUNE=2, NOIR=3, BLEU=4, BLANC=5 ;

    /** Definition du nom des couleurs */
    public static final String [] NOMCOULEURS = {"Vert","Rouge","Jaune","Noir","Bleu","Blanc"} ;

    /** Definition des couleurs */
    public static final Color [] COULEURS = {Color.GREEN, Color.RED, Color.YELLOW, Color.BLACK, Color.BLUE, Color.WHITE} ;
   
    // le panel sur lequel on dessine    
    private Dessin dessin ;

    // le panel avec les boutons pour choisir les outils
    private Panel outils ;

    // le panel du bas
    private Panel panelBas;

    // le panel pour le choix des couleurs du crayon
    private Panel choixCouleurs;

    // la couleur du crayon
    private Color couleur = Color.BLACK ;

    /** Lancement de l'application : instanciation d'un objet Paint */
    public static void main(String [] a) 
    { 
	Paint i = new Paint();
    }

    /** Ce constructeur cree la structure de l'interface graphique et associe des 
     *  auditeurs (listener) aux composants */
    public Paint() 
    {
	// cadre principal
	super("Dessinez !") ;
	setLayout(new BorderLayout()) ;
	setBounds(400,200,500,400) ;

	// On ajoute a l'application (en tant que Frame) un auditeur d'evenements de fenetre 
	// permettant de quitter proprement l'application quand l'utilisateur ferme la fenetre
	// N.B. : ceci est realise au moyen d'une CLASSE ANONYME derivee de WindowAdapter
	this.addWindowListener(new WindowAdapter() { // debut de la classe anonyme
		public void windowClosing(WindowEvent e) 
		{
		    System.exit(0) ;
		}
	    }) ;
	
	// creation de la zone de dessin
	dessin = new Dessin() ;
	dessin.setSize(400,400);

	// creation du panneau d'outils
	outils = new Panel() ;
	outils.setLayout(new GridLayout(5,2)) ;
	outils.add(new BoutonOutil(POINT)) ;
	outils.add(new BoutonOutil(LINE)) ;
	outils.add(new BoutonOutil(RECTANGLE)) ;

	// creation du panneau couleur du trait
	choixCouleurs = new Panel();
	choixCouleurs.setLayout(new FlowLayout());
	choixCouleurs.add(new BoutonCouleur(NOIR));
	choixCouleurs.add(new BoutonCouleur(VERT));
	choixCouleurs.add(new BoutonCouleur(ROUGE));
	choixCouleurs.add(new BoutonCouleur(JAUNE));
	choixCouleurs.add(new BoutonCouleur(BLANC));
	choixCouleurs.add(new BoutonCouleur(BLEU));

	// creation comboBox choix taille du trait
	/*String[] taille = new String[10];
	for (int i=5;i<15;i++)
	    taille[i]=""+i;
	    JComboBox boxTailleTrait = new JComboBox(taille);*/
	
	// creation panel bas
	panelBas = new Panel();
	panelBas.setLayout(new BorderLayout());
	panelBas.add(choixCouleurs, BorderLayout.CENTER);
	panelBas.add(boxTailleTrait, BorderLayout.WEST);
	

	// emboitement du panneau d'outils et de la zone de dessin dans le cadre principal
	this.add(outils, BorderLayout.WEST) ;
	this.add(dessin, BorderLayout.CENTER) ;
	this.add(panelBas, BorderLayout.SOUTH);

	this.setVisible(true) ;
    } // fin du constructeur

    // CLASSE INTERNE utilisee pour definir la zone de dessin (on est toujours a l'interieur
    // du code de la classe Paint) : cette classe a un acces direct a tout ce qui se trouve 
    // dans Paint (attributs et methodes), mais elle est elle-meme encapsulee dans Paint 
    // (donc en particulier, elle n'est pas accessible de l'exterieur)
    // Dessin est une sorte de Panel
    class Dessin extends Panel 
    {
	// les coordonnees des points de depart et d'arrivee 
	private int x0, y0, x, y ;

	// l'outil en cours d'utilisation (par defaut, dessin point par point)
	private int outil = POINT ;

	/** Le constructeur ajoute des auditeurs d'evenements souris au Panel courant */
	public Dessin() 
	{
	    // definition de la couleur de fond
	    this.setBackground(Color.WHITE) ;

	    // D'abord on ajoute pour la souris un auditeur qui reagit au clic, à l'enfoncement
	    // ou au relachement d'un bouton selon l'outil en cours
	    // N.B. : cet auditeur est realise au moyen d'une classe anonyme qui derive de 
	    // MouseAdapter
	    this.addMouseListener(new MouseAdapter() 
		{ // debut de la classe anonyme
		    public void mouseClicked(MouseEvent e) 
		    { // CLIC : appui + relachement sans deplacement 
			if (outil == POINT) 
			    { 
				x0 = e.getX() ; 
				y0 = e.getY() ;
				repaint() ;
			    }
		    }
		    public void mousePressed(MouseEvent e) 
		    { // PRESSION : appui sans relachement
			if ((outil == LINE) || (outil == RECTANGLE)) 
			    {
				x0 = e.getX() ; 
				y0 = e.getY() ;
			    }
		    }
		    public void mouseReleased(MouseEvent e) 
		    { // RELEASE : relachement apres appui
			if (outil == RECTANGLE) 
			    {
				x = e.getX() ; 
				y = e.getY() ;
				repaint() ;
			    }
		    }
		    // fin de la classe anonyme
		}) ;

	    // Puis meme chose avec un auditeur pour les evenements "mouvements de la souris" : 
	    // on s'interesse ici aux mouvements qui sont faits quand le bouton est enfonce
	    // N.B. : il s'agit d'une classe anonyme derivee de MouseMotionAdapter
	    this.addMouseMotionListener(new MouseMotionAdapter() 
		{ // debut de la classe anonyme
		    public void mouseDragged(MouseEvent e) 
		    {
			if (outil == LINE) 
			    {
				x = e.getX() ; 
				y = e.getY() ;
				repaint() ;
			    }
		    }
		}) ;
	} // fin du constructeur de Dessin

	// ici suite des méthodes de la classe interne Dessin : dessin d'un point, d'une
	// ligne, d'un rectangle, etc. sur un contexte graphique suppose connu
	// (ces methodes seront appelees par la methode paint)
	private void paintPoint(Graphics gc) 
	{
	    gc.fillRect(x0-1, y0-1, 3, 3) ;
	}

	private void paintLine(Graphics gc) 
	{
	    gc.drawLine(x0, y0, x, y) ;
	    x0 = x ; 
	    y0 = y ;
	}

	private void paintRectangle(Graphics gc) 
	{
	    gc.drawRect(Math.min(x0, x), Math.min(y0, y),
			Math.abs(x-x0), Math.abs(y-y0)) ;
	    x0 = x ; 
	    y0 = y ; 
	}
	
	/** Methode permettant de definir l'outil de dessin */
	public void setOutil(int o) 
	{ 
	    outil = o ; 
	}

	/** Methode permettant de definir la couleur du crayon */
	public void setCouleur(Color c) 
	{ 
	    couleur = c ; 
	}
	
	/** Redefinition de la methode update de Panel : on veut seulement appeler
	 * paint sans effacer (on rappelle que par defaut, update efface tout 
	 * avant d'appeler paint) */
	public void update(Graphics gc) 
	{ 
	    paint(gc) ; 
	}
	
	/** Redefinition de la methode paint de Panel : on appelle la methode de dessin
	 * correspondant a l'outil courant */
	public void paint(Graphics gc) 
	{
	    gc.setColor(couleur) ;
	    switch (outil) 
		{
		case POINT :
		    paintPoint(gc) ; break ;
		case LINE :
		    paintLine(gc) ; break ;
		case RECTANGLE :
		    paintRectangle(gc) ; break ;
		}
	}    
    } // fin de la classe interne Dessin

    
    // seconde classe interne, pour gerer les boutons de choix des outils
    class BoutonOutil extends Button 
    {
	// l'outil gere par le bouton
	private int outil_du_bouton = LINE ;
	
	/** Le constructeur cree un bouton portant le nom adequat et ajoute a l'instance
	 *  courante un auditeur pour reagir aux activations (clics) */
	public BoutonOutil(int outil_specifie) 
	{
	    super(OUTILS[outil_specifie]) ;
	    outil_du_bouton = outil_specifie ;
	    
	    // L'auditeur des actions du bouton : une classe anonyme implementant
	    // l'interface ActionListener
	    this.addActionListener(new ActionListener() 
		{
		    public void actionPerformed(ActionEvent e) 
		    {
			dessin.setOutil(outil_du_bouton) ;
		    }
		});    
	}
    } // fin de la classe interne BoutonOutil

    class BoutonCouleur extends Button 
    {
	// l'outil gere par le bouton
	private int couleur_du_bouton = NOIR ;
	
	/** Le constructeur cree un bouton portant le nom adequat et ajoute a l'instance
	 *  courante un auditeur pour reagir aux activations (clics) */
	public BoutonCouleur(int couleur_specifie) 
	{
	    super(NOMCOULEURS[couleur_specifie]) ;
	    couleur_du_bouton = couleur_specifie ;
	    this.setBackground(COULEURS[couleur_du_bouton]);
	    // L'auditeur des actions du bouton : une classe anonyme implementant
	    // l'interface ActionListener
	    this.addActionListener(new ActionListener() 
		{
		    public void actionPerformed(ActionEvent e) 
		    {
			dessin.setCouleur(COULEURS[couleur_du_bouton]) ;
		    }
		});    
	}
    } // fin de la classe interne BoutonOutil
    
} // fin de la classe Paint
