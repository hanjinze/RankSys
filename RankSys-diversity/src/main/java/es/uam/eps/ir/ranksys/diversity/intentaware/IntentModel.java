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
package es.uam.eps.ir.ranksys.diversity.intentaware;

import es.uam.eps.ir.ranksys.core.preference.PreferenceData;
import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import es.uam.eps.ir.ranksys.core.model.UserModel;
import es.uam.eps.ir.ranksys.core.model.UserModel.Model;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Intent-Aware model.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 * @param <F> type of the features
 */
public class IntentModel<U, I, F> extends UserModel<U> {

    private final PreferenceData<U, I, ?> totalData;
    private final FeatureData<I, F, ?> featureData;

    /**
     * Constructor that caches user intent-aware models.
     *
     * @param targetUsers user whose intent-aware models are cached
     * @param totalData preference data
     * @param featureData feature data
     */
    public IntentModel(Stream<U> targetUsers, PreferenceData<U, I, ?> totalData, FeatureData<I, F, ?> featureData) {
        super(targetUsers);
        this.totalData = totalData;
        this.featureData = featureData;
    }

    /**
     * Constructor that does not cache user intent-aware models.
     *
     * @param totalData preference data
     * @param featureData feature data
     */
    public IntentModel(PreferenceData<U, I, ?> totalData, FeatureData<I, F, ?> featureData) {
        super();
        this.totalData = totalData;
        this.featureData = featureData;
    }

    @Override
    protected UserIntentModel get(U user) {
        return new UserIntentModel(user);
    }

    @SuppressWarnings("unchecked")
    @Override
    public UserIntentModel getModel(U user) {
        return (UserIntentModel) super.getModel(user);
    }

    /**
     * User intent-aware model for {@link IntentModel}.
     */
    public class UserIntentModel implements Model<U> {

        private final Object2DoubleOpenHashMap<F> prob;

        /**
         * Constructor.
         *
         * @param user user whose model is created.
         */
        public UserIntentModel(U user) {
            Object2DoubleOpenHashMap<F> auxProb = new Object2DoubleOpenHashMap<>();
            auxProb.defaultReturnValue(0.0);

            int[] norm = {0};
            totalData.getUserPreferences(user).forEach(iv -> {
                featureData.getItemFeatures(iv.id).forEach(fv -> {
                    auxProb.addTo(fv.id, 1.0);
                    norm[0]++;
                });
            });

            if (norm[0] == 0) {
                norm[0] = featureData.numFeatures();
                featureData.getAllFeatures().sequential().forEach(f -> auxProb.put(f, 1.0));
            }

            auxProb.object2DoubleEntrySet().forEach(e -> {
                e.setValue(e.getDoubleValue() / norm[0]);
            });

            this.prob = auxProb;
        }

        /**
         * Returns the features considered in the intent model.
         *
         * @return the features considered in the intent model
         */
        public Set<F> getIntents() {
            return prob.keySet();
        }

        /**
         * Returns the features associated with an item.
         *
         * @param i item
         * @return the features associated with the item
         */
        public Stream<F> getItemIntents(I i) {
            return featureData.getItemFeatures(i).map(fv -> fv.id).filter(getIntents()::contains);
        }

        /**
         * Returns the probability of a feature in the model.
         *
         * @param f feature
         * @return probability of a feature in the model
         */
        public double p(F f) {
            return prob.getDouble(f);
        }

    }
}
