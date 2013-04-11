 <?php
/*
Template Name: Login
*/
?>

<?php include_once "head.php"; ?>
	
<div role="main" class="main">
	<div class="wrapper">
	
		<a href="/" class="site-logo site-logo-login">
			<img src="<?php bloginfo('template_directory'); ?>/img/dot.gif" height="1" width="1" alt="HDN - Health Developers Network" class="logo">
		</a>

		<div class="login-register-password">

			<?php global $user_ID, $user_identity; get_currentuserinfo(); if (!$user_ID) { ?>

			<ul class="tabs_login cf">
				<li class="active_login"><a href="#tab1_login">Login</a></li>
				<li><a href="#tab2_login">Register</a></li>
				<li><a href="#tab3_login">Forgot?</a></li>
			</ul>

			<div class="tab_container_login">
				<div id="tab1_login" class="tab_content_login">

					<?php $register = $_GET['register']; $reset = $_GET['reset']; if ($register == true) { ?>

					<h3>Success!</h3>
					<p>Check your email for the password and then return to log in.</p>

					<?php } elseif ($reset == true) { ?>

					<h3>Success!</h3>
					<p>Check your email to reset your password.</p>

					<?php } else { ?>

					<?php } ?>

					<form method="post" action="<?php bloginfo('url') ?>/wp-login.php" class="wp-user-form">
						<div class="username">
							<label for="user_login"><?php _e('Username'); ?>: </label>
							<input type="text" name="log" value="<?php echo esc_attr(stripslashes($user_login)); ?>" size="20" id="user_login" tabindex="11" />
						</div>
						<div class="password">
							<label for="user_pass"><?php _e('Password'); ?>: </label>
							<input type="password" name="pwd" value="" size="20" id="user_pass" tabindex="12" />
						</div>
						<div class="login_fields cf">
							<div class="rememberme">
								<label for="rememberme">
									<input type="checkbox" name="rememberme" value="forever" checked="checked" id="rememberme" tabindex="13" /> Remember me
								</label>
							</div>
							<?php do_action('login_form'); ?>
							<input type="submit" name="user-submit" value="<?php _e('Login'); ?>" tabindex="14" class="user-submit" />
							<input type="hidden" name="redirect_to" value="<?php echo $_SERVER['REQUEST_URI']; ?>" />
							<input type="hidden" name="user-cookie" value="1" />
						</div>
					</form>
				</div>

				<div id="tab2_login" class="tab_content_login" style="display:none;">
					<p>Sign up now to partisipate in the forums &amp; stay up to date.</p>
					<form method="post" action="<?php echo site_url('wp-login.php?action=register', 'login_post') ?>" class="wp-user-form">
						<div class="username">
							<label for="user_login"><?php _e('Username'); ?>: </label>
							<input type="text" name="user_login" value="<?php echo esc_attr(stripslashes($user_login)); ?>" size="20" id="user_login" tabindex="101" />
						</div>
						<div class="password">
							<label for="user_email"><?php _e('Your Email'); ?>: </label>
							<input type="text" name="user_email" value="<?php echo esc_attr(stripslashes($user_email)); ?>" size="25" id="user_email" tabindex="102" />
						</div>
						<div class="login_fields cf">
							<?php do_action('register_form'); ?>
							<input type="submit" name="user-submit" value="<?php _e('Sign up!'); ?>" class="user-submit" tabindex="103" />
							<?php $register = $_GET['register']; if($register == true) { echo '<p>Check your email for the password!</p>'; } ?>
							<input type="hidden" name="redirect_to" value="<?php echo $_SERVER['REQUEST_URI']; ?>?register=true" />
							<input type="hidden" name="user-cookie" value="1" />
						</div>
					</form>
				</div>

				<div id="tab3_login" class="tab_content_login" style="display:none;">
					<p>Enter your username or email below to reset your password:</p>
					<form method="post" action="<?php echo site_url('wp-login.php?action=lostpassword', 'login_post') ?>" class="wp-user-form">
						<div class="username">
							
							<input type="text" name="user_login" value="" size="20" id="user_login" tabindex="1001" class="user_email_full" />
						</div>
						<div class="login_fields cf">
							<?php do_action('login_form', 'resetpass'); ?>
							<input type="submit" name="user-submit" value="<?php _e('Reset my password'); ?>" class="user-submit" tabindex="1002" />
							<?php $reset = $_GET['reset']; if($reset == true) { echo '<p>A message will be sent to your email address.</p>'; } ?>
							<input type="hidden" name="redirect_to" value="<?php echo $_SERVER['REQUEST_URI']; ?>?reset=true" />
							<input type="hidden" name="user-cookie" value="1" />
						</div>
					</form>
				</div>
			</div>

			<div class="social_login">
			<?php do_action( 'wordpress_social_login' ); ?> 
			</div><!--end social login-->

				<?php } else { // is logged in ?>

			<div class="sidebox">
				<p>Welcome,</p>
				
				<div class="userinfo">
					<p>You&rsquo;re logged in as <strong><?php echo $user_identity; ?></strong></p>
					<p>
						<a href="<?php echo wp_logout_url('index.php'); ?>">Log out</a> | 
						<?php if (current_user_can('manage_options')) { 
							echo '<a href="' . admin_url() . '">' . __('Admin') . '</a>'; } else { 
							echo '<a href="' . admin_url() . 'profile.php">' . __('Profile') . '</a>'; } ?>

					</p>
				</div>
			</div>

			<?php } ?>
		</div><!--end login-register-password-->
	</div><!--end wrapper-->
</div><!--end main-->

<?php get_footer(); ?>		