
<?php get_header(); ?>
		
		<div role="main" class="main">
			<div class="page_title">
				<div class="wrapper">
					<hgroup>
						<h1>Library</h1>
						<h2>Browse and search developer information</h2>
					</hgroup>
				</div>
			</div><!--end page title-->
			
			<div class="main_content cf">
				<?php include_once "breadcrumbs.php"; ?>
				
				<div class="wrapper cf container">
					<div class="one_quarter tree_wrap">
						<?php include_once "tree_nav.php"; ?>
					</div>
					
					<div class="three_quarters">
						<div class="content_wrap">
							
							<?php query_posts( array( 'post_type' => 'library', 'orderby' => 'title', 'order' => 'asc', 'post_parent' => 0, ) ); ?>
							<?php if ( have_posts() ) : while ( have_posts() ) : the_post(); ?>
							
							<div class="half">
								<a href="<?php the_permalink(); ?>" class="box">
									<div class="line green_line"></div>
									<section class="nav_box cat_box green">
										<h2><?php the_title(); ?></h2>
										<p><?php the_excerpt(); ?></p>
									</section>
								</a>
							</div>
							
							<?php endwhile; endif; wp_reset_query(); ?>	
						</div><!--end content wrap-->	
					</div>
				</div><!--end wrapper-->
			</div><!--end main_content-->
		</div><!-- end main -->
		
<?php get_footer(); ?>		