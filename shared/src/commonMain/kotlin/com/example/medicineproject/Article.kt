package com.example.medicineproject

data class Article(
    val id: String,
    val title: String,
    val description: String,
    val author: String,
    val category: String
)
val mockArticles = listOf(
    Article(
        id = "38123456",
        title = "Genome analysis and evolutionary dynamics of RNA viruses in the 21st century",
        description = "This study outlines modern high-throughput sequencing approaches for RNA viruses, focusing on fast mutation mechanisms.",
        author = "Andrey L. S.",
        category = "Science"
    ),
    Article(
        id = "38234567",
        title = "Impact of artificial intelligence on diagnostic accuracy in clinical oncology",
        description = "Evaluation of deep learning frameworks in analyzing MRI and CT imagery for early-stage tumor detection.",
        author = "Smith J. A.",
        category = "Science"
    ),
    Article(
        id = "38345678",
        title = "Novel inflammatory biomarkers in chronic cardiovascular disease progression",
        description = "A comprehensive multi-center cohort analysis exploring specific circulating protein profiles useful for predicting acute coronary events.",
        author = "Lee K. M.",
        category = "Medicine"
    ),
    Article(
        id = "38456789",
        title = "CRISPR-Cas9 genome editing: updates on ongoing global human clinical trials",
        description = "A systematic review tracking therapeutic efficacy, off-target mutations, and safety considerations in current gene-editing applications.",
        author = "Garcia M. E.",
        category = "Genetics"
    ),
    Article(
        id = "38567890",
        title = "The gut microbiome axis and its direct modulation of T-cell mediated immune responses",
        description = "Investigating how metabolic byproducts of distinct bacterial taxonomy influence the host adaptive immune system.",
        author = "Voynov R.",
        category = "Science"
    ),
    Article(
        id = "38678901",
        title = "Efficacy of novel mRNA-based vaccines against emerging zoonotic respiratory pathogens",
        description = "Clinical evaluation of multi-valent mRNA platforms in eliciting neutralizing antibody titers in preclinical animal models.",
        author = "Dupont L.",
        category = "Virology"
    ),
    Article(
        id = "38789012",
        title = "Neuroinflammation pathways in the pathogenesis of Alzheimer's disease",
        description = "Investigating microglial activation thresholds and tau protein propagation dynamics in aging cerebral cortex structures.",
        author = "Müller A. H.",
        category = "Neuroscience"
    ),
    Article(
        id = "38890123",
        title = "Applications of single-cell RNA sequencing in mapping human cardiac fibrosis",
        description = "High-resolution cellular mapping of myocardial fibroblast transition phases post-acute myocardial infarction.",
        author = "Sato T.",
        category = "Cardiology"
    ),
    Article(
        id = "38901234",
        title = "Pharmacogenomics-driven therapeutic strategies for resistant arterial hypertension",
        description = "A clinical trial optimizing drug delivery algorithms based on individual nucleotide polymorphisms in renin-angiotensin pathways.",
        author = "Brown T. D.",
        category = "Medicine"
    ),
    Article(
        id = "39012345",
        title = "Metabolic reprogramming of cancer cells under hypoxic tumor microenvironments",
        description = "Elucidating the role of HIF-1 alpha stabilization in inducing glycolytic shift and chemotherapy resistance mechanisms.",
        author = "Chen L. X.",
        category = "Oncology"
    ),
    Article(
        id = "39123456",
        title = "Long-term neurological sequelae following acute viral encephalitis infections",
        description = "A retrospective longitudinal study assessing cognitive deficits and white matter integrity changes via diffusion tensor imaging.",
        author = "Ivanov I. P.",
        category = "Virology"
    ),
    Article(
        id = "39234567",
        title = "Targeting epigenetic modifications in pediatric acute lymphoblastic leukemia",
        description = "Evaluating histone deacetylase inhibitors as potential adjuvant therapeutic agents to standard multi-agent chemotherapy regimens.",
        author = "Taylor R. E.",
        category = "Oncology"
    ),
    Article(
        id = "39345678",
        title = "Biomechanical properties of synthetic hydrogel scaffolds for articular cartilage repair",
        description = "Testing viscoelastic performance and stem cell differentiation compatibility of advanced cross-linked polymer matrices.",
        author = "Kim D. H.",
        category = "Bioengineering"
    ),
    Article(
        id = "39456789",
        title = "The role of circadian rhythm disruption in metabolic syndrome development",
        description = "Analyzing peripheral molecular clock de-synchronization and its direct cascading effects on insulin sensitivity indices.",
        author = "Wilson P. J.",
        category = "Science"
    ),
    Article(
        id = "39567890",
        title = "Nanoparticle-mediated targeted drug delivery systems across the blood-brain barrier",
        description = "Development of liposomal vectors conjugated with transferrin receptors for localized drug release in glioblastoma models.",
        author = "Martinez S.",
        category = "Bioengineering"
    ),
    Article(
        id = "39678901",
        title = "Genetic architecture of early-onset familial Parkinson's disease cohorts",
        description = "Whole-exome sequencing analysis identifying rare structural variants in non-classical pathways among affected populations.",
        author = "Orale R.",
        category = "Genetics"
    ),
    Article(
        id = "39789012",
        title = "Automated classification of diabetic retinopathy using lightweight edge-AI algorithms",
        description = "Deploying optimized deep neural networks on low-power mobile devices for rural community screening applications.",
        author = "Patel A. V.",
        category = "Science"
    ),
    Article(
        id = "39890123",
        title = "Microplastics detection in human vascular tissues and associated endothelial risk",
        description = "A pioneering pilot study quantifying plastic polymer aggregation in femoral arteries via Raman microspectroscopy.",
        author = "Wright L. C.",
        category = "Medicine"
    ),
    Article(
        id = "39901234",
        title = "Therapeutic potential of fecal microbiota transplantation in severe ulcerative colitis",
        description = "A randomized controlled trial investigating mucosal healing rates and cytokine down-regulation post-transplantation.",
        author = "O'Connor B.",
        category = "Medicine"
    ),
    Article(
        id = "40012345",
        title = "Structural basis of broad-spectrum neutralization by human monoclonal antibodies",
        description = "Cryo-EM structural resolution of conserved epitope binding sites on highly mutable viral surface glycoproteins.",
        author = "Schmidt M.",
        category = "Virology"
    )
)
