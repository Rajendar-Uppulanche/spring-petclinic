require 'rails_helper'

RSpec.describe VisitsController, type: :controller do
  let(:pet) { create(:pet) }
  let(:veterinarian) { create(:veterinarian) }
  let(:valid_attributes) {
    { pet_id: pet.id, veterinarian_id: veterinarian.id, visit_date: Date.today, reason: "Routine checkup", notes: "Healthy pet" }
  }
  let(:invalid_attributes) {
    { pet_id: nil, veterinarian_id: nil, visit_date: nil, reason: "" }
  }

  # Assuming a user is logged in for controller tests
  before do
    # For simplicity, assuming no authentication or a dummy user
    # If authentication is present, add `sign_in create(:user)` or similar
  end

  describe "POST #create" do
    context "with valid parameters" do
      it "creates a new Visit" do
        expect {
          post :create, params: { visit: valid_attributes }
        }.to change(Visit, :count).by(1)
      end

      it "creates a new Visit with weight" do
        expect {
          post :create, params: { visit: valid_attributes.merge(weight: 15.2) }
        }.to change(Visit, :count).by(1)
        expect(Visit.last.weight).to eq(15.2)
      end

      it "redirects to the created visit" do
        post :create, params: { visit: valid_attributes }
        expect(response).to redirect_to(Visit.last)
      end
    end

    context "with invalid parameters" do
      it "does not create a new Visit" do
        expect {
          post :create, params: { visit: invalid_attributes }
        }.to_not change(Visit, :count)
      end

      it "renders a :new template (or unprocessable_entity status)" do
        post :create, params: { visit: invalid_attributes }
        expect(response).to have_http_status(:unprocessable_entity) # or :success if rendering new template
      end

      it "does not create a new Visit with invalid weight" do
        expect {
          post :create, params: { visit: valid_attributes.merge(weight: -10.0) }
        }.to_not change(Visit, :count)
      end
    end
  end

  describe "PATCH #update" do
    let!(:visit) { create(:visit, valid_attributes) } # Create a visit for updating

    context "with valid parameters" do
      let(:new_attributes) {
        { reason: "Follow-up checkup", weight: 16.0 }
      }

      it "updates the requested visit" do
        patch :update, params: { id: visit.to_param, visit: new_attributes }
        visit.reload
        expect(visit.reason).to eq("Follow-up checkup")
        expect(visit.weight).to eq(16.0)
      end

      it "redirects to the visit" do
        patch :update, params: { id: visit.to_param, visit: new_attributes }
        expect(response).to redirect_to(visit)
      end
    end

    context "with invalid parameters" do
      it "renders a :edit template (or unprocessable_entity status)" do
        patch :update, params: { id: visit.to_param, visit: invalid_attributes }
        expect(response).to have_http_status(:unprocessable_entity)
      end

      it "does not update the visit with invalid weight" do
        original_weight = visit.weight
        patch :update, params: { id: visit.to_param, visit: { weight: -5.0 } }
        visit.reload
        expect(visit.weight).to eq(original_weight) # Weight should not have changed
      end
    end
  end

  # Other existing controller tests (index, show, edit, destroy)
end