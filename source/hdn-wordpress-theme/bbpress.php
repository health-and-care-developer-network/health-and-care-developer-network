<?php get_header(); ?>
		

		<?php if ( have_posts() ) : while ( have_posts() ) : the_post(); ?>
		<div role="main" class="main <?php bbp_forum_title(); ?>">
			<div class="page_title">
				<div class="wrapper">
					<hgroup class="<?php bbp_forum_title(); ?>">
						
						<?php
							$forum_title = the_title('', '', 0);
							$parent_title = get_the_title($post->post_parent);
							if ($forum_title == "Market Square"){
								echo '<h1>Market Square</h1><h2>Share ideas, problems and expertise.</h2>';
							} 
							elseif ($parent_title == "Market Square") {
								echo '<h1>Market Square</h1><h2>Share ideas, problems and expertise.</h2>';
							}
							else {
								echo '<h1>Community Forums</h1><h2>Ask and respond to questions; get involved with the community.</h2>';
							}
						?>

					</hgroup>
				</div>
			</div><!--end page title-->
			
			<div class="main_content cf">
				<?php include_once "breadcrumbs.php"; ?>

				<div class="wrapper cf container">
					
					
						<div class="content_wrap cf">
							
							<div class="three_quarters forums-wrap">
								<div class="forums-inner">
									<hgroup>
										<h2><?php the_title(); ?></h2>
										<h3 class="delta"><?php the_field('sub_title'); ?></h3>
									</hgroup>
									
									<p><a href="http://<?php echo $_SERVER["HTTP_HOST"] . $_SERVER["REQUEST_URI"] ?>feed">RSS Feed</a></p>

									<?php the_content(); ?>
								</div><!--end forums-inner-->
							</div><!--end three quarters-->
							
							<aside class="one_quarter link_lists">
								<div class="list">
									<ul class="attachments <?php the_sub_field('ll_title'); ?>">

										<?php  if (is_user_logged_in()){
										global $current_user; get_currentuserinfo();
										echo('Welcome, ' . $current_user->user_login . '<p>Enjoy the forums!</p>');
										}
										else {
										echo '' . do_action( 'wordpress_social_login' ) . '';
										};
										?>
						
									</ul>
								</div><!--end list-->
								<?php include_once "share_tools.php"; ?>
							</aside><!--end one quarter-->	
						</div><!--end content wrap-->
						<?php endwhile; endif; ?>
					
					
				</div><!--end wrapper-->
			</div><!--end main_content-->
		</div><!-- end main -->
		
<?php get_footer(); ?>
