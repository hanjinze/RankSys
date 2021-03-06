/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autonoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.uam.eps.ir.ranksys.core.index;

import java.util.stream.Stream;

/**
 * Index for a set of features.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <F> type of the features
 */
public interface FeatureIndex<F> {

    /**
     * Checks whether the index contains a feature.
     *
     * @param f feature
     * @return true if the index contains the feature, false otherwise
     */
    public boolean containsFeature(F f);

    /**
     * Counts the number of indexed features.
     *
     * @return the total number of features
     */
    public int numFeatures();

    /**
     * Retrieves a stream of the indexed features.
     *
     * @return a stream of all the features
     */
    public Stream<F> getAllFeatures();

}
