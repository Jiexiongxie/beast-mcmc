/*
 * ContinuousTraitData.java
 *
 * Copyright (c) 2002-2016 Alexei Drummond, Andrew Rambaut and Marc Suchard
 *
 * This file is part of BEAST.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership and licensing.
 *
 * BEAST is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 *  BEAST is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with BEAST; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
 * Boston, MA  02110-1301  USA
 */

package dr.evomodel.treedatalikelihood.continuous;

import dr.evomodel.treedatalikelihood.continuous.cdi.PrecisionType;
import dr.inference.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marc A. Suchard
 */
public class ElementaryVectorDataModel extends AbstractModel implements ContinuousTraitPartialsProvider {

    private final Parameter tipIndicator;
    private final Parameter dimIndicator;

    private final int numTraits;
    private final int numTips;
    private final int dimTrait;

    private final PrecisionType precisionType;

    public ElementaryVectorDataModel(String name,
                                     Parameter tipIndicator,
                                     Parameter dimIndicator,
                                     final int numTips, final int dimTrait,
                                     PrecisionType precisionType) {
        super(name);
        this.tipIndicator = tipIndicator;
        addVariable(tipIndicator);

        this.dimIndicator = dimIndicator;
        if (dimIndicator != null) {
            addVariable(dimIndicator);
        }

        this.numTraits = tipIndicator.getDimension();
        this.numTips = numTips;
        this.dimTrait = dimTrait;
        this.precisionType = precisionType;
    }

    public boolean bufferTips() { return true; }

    public int getTraitCount() {  return numTraits; }

    public int getTraitDimension() { return dimTrait; }

    public PrecisionType getPrecisionType() {
        return precisionType;
    }

    public CompoundParameter getParameter() { return traitParameter; }

    public List<Integer> getMissingIndices() { return noMissingIndices; }

    @Override
    protected void handleModelChangedEvent(Model model, Object object, int index) {
        // No sub-models
    }

    @Override
    protected void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
        if (variable == tipIndicator ||
                variable == dimIndicator) {
            fireModelChanged(this);
        }
    }

    @Override
    protected void storeState() { }

    @Override
    protected void restoreState() { }

    @Override
    protected void acceptState() { }

    public double[] getTipPartial(int taxonIndex, boolean fullyObserved) {

        final int offsetInc = dimTrait + precisionType.getMatrixLength(dimTrait);
        final double[] partial = new double[numTraits * offsetInc]; // zeros all values
        final double precision = PrecisionType.getObservedPrecisionValue(false);

        int offset = 0;
        for (int trait = 0; trait < numTraits; ++trait) {

            if (taxonIndex == getCurrentTipIndex(trait)) {
                int dim = getCurrentDimIndex(trait);
                partial[offset + dim] = 1.0;
            }

            precisionType.fillPrecisionInPartials(partial, offset, 0, precision, dimTrait);
            offset += offsetInc;
        }

        return partial;
    }

    private int getCurrentTipIndex(int trait) {
        return (int) tipIndicator.getParameterValue(trait);
    }

    private int getCurrentDimIndex(int trait) {
        return dimIndicator == null ? 0 : (int) dimIndicator.getParameterValue(trait);
    }

    private CompoundParameter traitParameter = new CompoundParameter("elementaryVector") {
        @Override
        public Parameter getParameter(int index) {
            return null;
        }

        @Override
        public double getParameterValue(int index) {
            throw new RuntimeException("Not yet implemented");
        }

        @Override
        public double[] getParameterValues() {

            double[] result = new double[numTraits * numTips * dimTrait];
            for (int trait = 0; trait < numTraits; ++trait) {

                int tip = getCurrentTipIndex(trait);
                int dim = getCurrentDimIndex(trait);

                result[tip * numTraits * dimTrait + trait * dimTrait + dim] = 1.0;
            }

            return result;
        }
    };

    private static List<Integer> noMissingIndices = new ArrayList<Integer>();
}
