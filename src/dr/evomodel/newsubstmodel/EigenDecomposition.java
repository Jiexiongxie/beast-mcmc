package dr.evomodel.newsubstmodel;

import java.util.Arrays;

/**
 * @author Andrew Rambaut
 * @author Alexei Drummond
 * @version $Id$
 */
public class EigenDecomposition {

    public EigenDecomposition(double[][] evec, double[][] ievc, double[] eval) {
        Evec = evec;
        Ievc = ievc;
        Eval = eval;
    }

    public EigenDecomposition copy() {
        double[][] evec = Evec.clone();
        double[][] ievc = Ievc.clone();
        double[] eval = Eval.clone();

        for (int i = 0; i < evec.length; i++) {
            evec[i] = Evec[i].clone();
            ievc[i] = Ievc[i].clone();
        }

        return new EigenDecomposition(evec, ievc, eval);
    }

    /**
     * This function returns the Eigen vectors.
     * @return the array
     */
    public final double[][] getEigenVectors() {
        return Evec;
    }

    /**
     * This function returns the inverse Eigen vectors.
     * @return the array
     */
    public final double[][] getInverseEigenVectors() {
        return Ievc;
    }

    /**
     * This function returns the Eigen values.
     * @return the Eigen values
     */
    public final double[] getEigenValues() {
        return Eval;
    }

    // Eigenvalues, eigenvectors, and inverse eigenvectors
    private final double[][] Evec;
    private final double[][] Ievc;
    private final double[] Eval;

}
