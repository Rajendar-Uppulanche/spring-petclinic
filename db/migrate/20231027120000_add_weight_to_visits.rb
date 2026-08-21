class AddWeightToVisits < ActiveRecord::Migration[7.0]
  def change
    add_column :visits, :weight, :float
  end
end