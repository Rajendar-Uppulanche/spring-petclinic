from flask_wtf import FlaskForm
from wtforms import StringField, SubmitField
from wtforms.validators import DataRequired, Length


class EditVisitForm(FlaskForm):
    description = StringField("Description", validators=[DataRequired(), Length(min=1, max=200)])
    submit = SubmitField("Update Visit")
