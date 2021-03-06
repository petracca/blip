package ch.idsia.blip.api.old;


import ch.idsia.blip.api.Api;
import ch.idsia.blip.core.common.BayesianNetwork;
import ch.idsia.blip.core.common.DataSet;
import ch.idsia.blip.core.common.score.BDeu;
import ch.idsia.blip.core.utils.other.IncorrectCallException;
import ch.idsia.blip.core.utils.data.SIntSet;
import org.kohsuke.args4j.Option;

import java.util.logging.Logger;

import static ch.idsia.blip.core.utils.other.RandomStuff.getBayesianNetwork;
import static ch.idsia.blip.core.utils.other.RandomStuff.getDataSet;


/**
 * BDeu Accountant
 */

public class Bda extends Api {

    private static final Logger log = Logger.getLogger(Bda.class.getName());

    @Option(name = "-n", required = true, usage = "Bayesian network file path")
    private String ph;

    @Option(name = "-d", required = true, usage = "Datafile path (.dat format)")
    private String s_datafile;

    @Option(name = "-a", required = true, usage = "Equivalent sample size")
    private double alpha = 1;


    /**
     * Command line execution
     *
     * @param args parameters provided
     * @throws IncorrectCallException if there is pa problem with parameters
     */
    public static void main(String[] args) throws IncorrectCallException {
        defaultMain(args, new Bda(), log);
    }

    @Override
    public void exec() throws Exception {

        BayesianNetwork bn = getBayesianNetwork(ph);
        DataSet dat = getDataSet(s_datafile);

        double sum_ll = computeBDeu(bn, dat, alpha);

        System.out.println(sum_ll);
    }

    // Computed BDeu score of the given Bayesian network over the given data
    private double computeBDeu(BayesianNetwork bn, DataSet dat, double alpha) {

        BDeu bDeu = new BDeu(this.alpha, dat);

        double tot_sk = 0;

        for (int n = 0; n < dat.n_var; n++) {

            int[] pars = bn.parents(n);

            double sk;

            if (pars.length > 0) {
                int[][] p_values = bDeu.computeParentSetValues(pars);

                sk = bDeu.computeScore(n, pars);
            } else {
                sk = bDeu.computeScore(n);
            }

            System.out.printf("%d # %s%n", n, sk);

            tot_sk += sk;

        }

        return tot_sk;
    }

}
