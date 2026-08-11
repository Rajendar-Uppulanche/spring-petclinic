from flask import Flask, render_template, redirect, url_for, flash
from flask_sqlalchemy import SQLAlchemy
from flask_migrate import Migrate
from flask_login import LoginManager, UserMixin, login_user, logout_user, login_required, current_user
from flask_wtf.csrf import CSRFProtect
from werkzeug.security import generate_password_hash, check_password_hash
import logging

# Import blueprints
from controllers.owner_controller import owner_bp
from controllers.pet_controller import pet_bp
from controllers.visit_controller import visit_bp
from controllers.auth_controller import auth_bp


app = Flask(__name__)
app.config['SECRET_KEY'] = 'your_secret_key_here' # Change this in production!
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///petclinic.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

# Load settings from config.settings if available
try:
    from config import settings
    app.config.update({
        'SQLALCHEMY_DATABASE_URI': getattr(settings, 'SQLALCHEMY_DATABASE_URI', app.config['SQLALCHEMY_DATABASE_URI']),
        'PAYMENT_RETRY_LIMIT': getattr(settings, 'PAYMENT_RETRY_LIMIT', 3)
    })
except ImportError:
    logging.warning("config.settings not found. Using default configurations.")


db = SQLAlchemy(app)
migrate = Migrate(app, db)
crsf = CSRFProtect(app)

login_manager = LoginManager()
login_manager.init_app(app)
login_manager.login_view = 'auth.login'

# Import models after db is initialized
from app.models.user import User
from app.models.owner import Owner
from app.models.pet import Pet
from app.models.visit import Visit
from app.models.veterinarian import Veterinarian


@login_manager.user_loader
def load_user(user_id):
    return User.query.get(int(user_id))


@app.route('/')
def index():
    if current_user.is_authenticated:
        return redirect(url_for('owner.list'))
    return render_template('index.html')


# Register blueprints
app.register_blueprint(owner_bp)
app.register_blueprint(pet_bp)
app.register_blueprint(visit_bp)
app.register_blueprint(auth_bp)


# Setup logging
logging.basicConfig(level=logging.INFO)

if __name__ == '__main__':
    with app.app_context():
        db.create_all()
    app.run(debug=True)
